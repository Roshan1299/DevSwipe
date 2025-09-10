
package com.first.devswipe.controller

import com.first.devswipe.service.FileStorageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/upload")
class FileUploadController(private val fileStorageService: FileStorageService) {

    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        val fileName = fileStorageService.storeFile(file)
        val fileDownloadUri = "/uploads/$fileName"
        return ResponseEntity.ok(mapOf("url" to fileDownloadUri))
    }
}
