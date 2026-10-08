package com.sena.Backend_OneLanguage.translation.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TranslationRecordingStorage {
    private static final long MAX_RECORDING_SIZE = 25L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("video/webm", "video/mp4");

    private final Path storageRoot;

    public TranslationRecordingStorage(@Value("${app.media.storage-path:uploads/translation-recordings}") String storagePath) {
        this.storageRoot = Paths.get(storagePath).toAbsolutePath().normalize();
    }

    public StoredRecording store(UUID translationId, MultipartFile recording) {
        if (recording == null || recording.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La grabacion es obligatoria");
        }
        if (recording.getSize() > MAX_RECORDING_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "La grabacion supera 25 MB");
        }

        String contentType = recording.getContentType();
        if (contentType == null || ALLOWED_CONTENT_TYPES.stream().noneMatch(contentType::startsWith)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Formato de video no permitido");
        }

        String extension = contentType.startsWith("video/mp4") ? ".mp4" : ".webm";
        String fileName = translationId + extension;
        Path target = resolve(fileName);
        try {
            Files.createDirectories(storageRoot);
            try (var input = recording.getInputStream()) {
                Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar la grabacion", exception);
        }
        return new StoredRecording(fileName, contentType, recording.getSize());
    }

    public Resource load(String fileName) {
        Path file = resolve(fileName);
        if (!Files.isRegularFile(file)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grabacion no encontrada");
        }
        return new FileSystemResource(file);
    }

    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Files.deleteIfExists(resolve(fileName));
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo eliminar la grabacion", exception);
        }
    }

    private Path resolve(String fileName) {
        Path resolved = storageRoot.resolve(fileName).normalize();
        if (!resolved.startsWith(storageRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ruta de grabacion invalida");
        }
        return resolved;
    }

    public record StoredRecording(String path, String contentType, long size) {}
}
