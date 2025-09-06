
package com.first.devswipe.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class FileStorageService {

    private val uploadDir: Path = Paths.get("uploads")

    init {
        if (!Files.exists(uploadDir)) {
            Files.createDirectory(uploadDir)
        }
    }

    fun storeFile(file: MultipartFile): String {
        val fileName = "${UUID.randomUUID()}-${file.originalFilename}"
        val targetLocation = uploadDir.resolve(fileName)
        Files.copy(file.inputStream, targetLocation)
        return fileName
    }
}
