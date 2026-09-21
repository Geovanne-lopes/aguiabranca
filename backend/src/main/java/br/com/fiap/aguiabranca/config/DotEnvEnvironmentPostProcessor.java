package br.com.fiap.aguiabranca.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Carrega {@code .env} local sem sobrescrever variáveis já definidas no SO/IDE.
 */
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = resolveEnvFile();
        if (envFile == null) {
            return;
        }
        Map<String, Object> values = parse(envFile);
        if (values.isEmpty()) {
            return;
        }
        Map<String, Object> absentOrBlank = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String existing = environment.getProperty(entry.getKey());
            if (existing == null || existing.isBlank()) {
                absentOrBlank.put(entry.getKey(), entry.getValue());
            }
        }
        if (!absentOrBlank.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", absentOrBlank));
        }
    }

    private Path resolveEnvFile() {
        Path cwd = Path.of(System.getProperty("user.dir", "."));
        Path direct = cwd.resolve(".env");
        if (Files.isRegularFile(direct)) {
            return direct;
        }
        Path nested = cwd.resolve("backend").resolve(".env");
        if (Files.isRegularFile(nested)) {
            return nested;
        }
        return null;
    }

    private Map<String, Object> parse(Path file) {
        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String value = unquote(line.substring(eq + 1).trim());
                if (!key.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (IOException ignored) {
            return Map.of();
        }
        return values;
    }

    private String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
