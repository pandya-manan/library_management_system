package com.oops.library.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(System.getProperty("user.dir"), uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not initialize upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "file");
        String cleanedFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String uniqueFilename = UUID.randomUUID() + "_" + cleanedFilename;

        Path targetDirectory = (subDirectory == null || subDirectory.isBlank())
                ? rootLocation
                : rootLocation.resolve(subDirectory).normalize();

        try {
            Files.createDirectories(targetDirectory);
            Path destinationFile = targetDirectory.resolve(uniqueFilename).normalize();
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            Path relativePath = rootLocation.relativize(destinationFile);
            return "/uploads/" + relativePath.toString().replace('\\', '/');
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store file", ex);
        }
    }

    public void deleteFile(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return;
        }

        String normalizedPath = storedPath.replaceFirst("^/", "");
        Path absolutePath = Paths.get(System.getProperty("user.dir"), normalizedPath);
        try {
            Files.deleteIfExists(absolutePath);
        } catch (IOException ex) {
            // Ignore delete failure silently for now; could be logged in future.
        }
    }
}


