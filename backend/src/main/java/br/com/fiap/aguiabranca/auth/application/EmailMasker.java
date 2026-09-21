package br.com.fiap.aguiabranca.auth.application;

public final class EmailMasker {

    private EmailMasker() {
    }

    public static String mask(String email) {
        if (email == null || email.isBlank()) {
            return "***";
        }
        String normalized = email.trim().toLowerCase();
        int at = normalized.indexOf('@');
        if (at <= 0) {
            return normalized.charAt(0) + "***";
        }
        return normalized.charAt(0) + "***" + normalized.substring(at);
    }
}
