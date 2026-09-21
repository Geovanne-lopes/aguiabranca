package br.com.fiap.aguiabranca.ops;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RepositorySecretsTest {

    @Test
    @DisplayName("A-OPS-02 .env.example existe e o repositório não traz secrets reais")
    void aOps02_envExampleWithoutRealSecrets() throws IOException {
        Path root = resolveRepoRoot();
        Path example = root.resolve("backend").resolve(".env.example");
        assertThat(example).exists();

        String exampleContent = Files.readString(example, StandardCharsets.UTF_8);
        assertThat(exampleContent).contains("JWT_SECRET=substitua-por-32bytes-minimo-de-segredo");
        assertThat(exampleContent).contains("GEMINI_API_KEY=");
        assertThat(exampleContent).doesNotContain("AIza");

        Path gitignore = root.resolve(".gitignore");
        Path backendGitignore = root.resolve("backend").resolve(".gitignore");
        String ignore = "";
        if (Files.exists(gitignore)) {
            ignore += Files.readString(gitignore, StandardCharsets.UTF_8);
        }
        if (Files.exists(backendGitignore)) {
            ignore += "\n" + Files.readString(backendGitignore, StandardCharsets.UTF_8);
        }
        assertThat(ignore).contains(".env");

        try (Stream<Path> walk = Files.walk(root.resolve("backend"))) {
            List<Path> suspects = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> !isIgnored(path))
                    .filter(this::looksLikeConfig)
                    .filter(this::containsRealSecret)
                    .toList();
            assertThat(suspects).isEmpty();
        }
    }

    private boolean isIgnored(Path path) {
        String value = path.toString().replace('\\', '/');
        return value.contains("/target/") || value.contains("/.idea/");
    }

    private Path resolveRepoRoot() {
        Path cwd = Path.of("").toAbsolutePath();
        if (Files.exists(cwd.resolve("backend").resolve(".env.example"))) {
            return cwd;
        }
        if (Files.exists(cwd.resolve(".env.example"))) {
            return cwd.getParent();
        }
        throw new IllegalStateException("Não foi possível localizar backend/.env.example a partir de " + cwd);
    }

    private boolean looksLikeConfig(Path path) {
        String name = path.getFileName().toString();
        return name.endsWith(".yml")
                || name.endsWith(".yaml")
                || name.endsWith(".properties")
                || name.equals(".env")
                || name.equals(".env.example");
    }

    private boolean containsRealSecret(Path path) {
        try {
            String googleKeyPrefix = "AIza" + "Sy";
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.contains(googleKeyPrefix)) {
                return true;
            }
            String placeholder = "substitua-por-32bytes-minimo-de-segredo";
            return content.lines().anyMatch(line -> {
                String trimmed = line.trim();
                if (!trimmed.startsWith("JWT_SECRET=")) {
                    return false;
                }
                String value = trimmed.substring("JWT_SECRET=".length()).trim();
                return !value.isEmpty()
                        && !placeholder.equals(value)
                        && !value.startsWith("${")
                        && !value.contains("test-jwt-secret");
            });
        } catch (IOException ex) {
            return false;
        }
    }
}
