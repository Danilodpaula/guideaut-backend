package com.guideaut.project.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

@Service
public class AvatarService {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    /**
     * Tipos de imagem permitidos.
     * Obs: inclui "image/jpg" porque alguns navegadores/serviços usam isso.
     */
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp");

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/jpg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    public String saveUserAvatar(UUID userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem é obrigatório");
        }

        // Normaliza o content-type pra evitar surpresas de maiúsculas/minúsculas
        String contentType = Optional.ofNullable(file.getContentType())
                .map(ct -> ct.toLowerCase(Locale.ROOT))
                .orElse("");

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            // Aqui você pode logar o contentType pra debug se quiser
            // ex: log.warn("Tipo de imagem não permitido: {}", contentType);
            throw new IllegalArgumentException("Formato de imagem inválido. Use JPEG, PNG ou WEBP");
        }

        String ext = EXT_BY_CONTENT_TYPE.getOrDefault(contentType, "");
        if (ext.isEmpty()) {
            // fallback de segurança
            throw new IllegalArgumentException("Não foi possível determinar a extensão da imagem");
        }

        // Diretório base (configurável via app.uploads.dir)
        Path root = Paths.get(uploadsDir).toAbsolutePath().normalize();
        Path avatars = root.resolve("avatars");
        Files.createDirectories(avatars);

        // Sempre salva com o mesmo nome por usuário (sobrescreve)
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
        if (avatarPath == null || avatarPath.isBlank()) {
            return;
        }

        Path root = Paths.get(uploadsDir).toAbsolutePath().normalize();
        Path file = root.resolve(avatarPath).normalize();
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // Se quiser, dá pra logar isso, mas não deve quebrar o fluxo do usuário
            // log.warn("Falha ao deletar avatar antigo {}", avatarPath, e);
        }
    }
}
