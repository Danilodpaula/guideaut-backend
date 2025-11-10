package com.guideaut.project.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class AvatarService {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp");

    public String saveUserAvatar(UUID userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem é obrigatório");
        }
        final String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new IllegalArgumentException("Formato inválido. Use JPEG, PNG ou WEBP");
        }

        final String ext = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };

        Path root = Paths.get(uploadsDir).toAbsolutePath().normalize();
        Path avatars = root.resolve("avatars");
        Files.createDirectories(avatars);

        String fileName = userId.toString() + ext;
        Path target = avatars.resolve(fileName);

        // sobrescreve o arquivo anterior, se existir
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // caminho relativo sob /files/**
        return "avatars/" + fileName;
    }

    public void deleteUserAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank())
            return;
        Path root = Paths.get(uploadsDir).toAbsolutePath().normalize();
        Path file = root.resolve(avatarPath).normalize();
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
        }
    }
}
