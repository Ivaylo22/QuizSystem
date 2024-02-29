package sit.tuvarna.bg.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sit.tuvarna.bg.core.processor.external.StorageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/")
public class FileUploadController {

    private final StorageService storageService;


    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @RequestParam("userEmail") String userEmail) {
        try {
            String presignedUrl = storageService.uploadUserAvatar(file, userEmail);

            return ResponseEntity.ok().body(presignedUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload avatar due to an IO error.");
        }
    }

    @PostMapping("/upload-question-image")
    public ResponseEntity<?> uploadQuestionImage(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("questionId") String questionId) {
        try {
            String presignedUrl = storageService.uploadQuestionImage(file, questionId);

            return ResponseEntity.ok().body(presignedUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload avatar due to an IO error.");
        }
    }
}
