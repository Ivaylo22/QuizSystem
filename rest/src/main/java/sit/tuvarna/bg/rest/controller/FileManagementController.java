package sit.tuvarna.bg.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.tuvarna.bg.core.externalservices.FileOperationsService;
import sit.tuvarna.bg.persistence.entity.Quiz;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileManagementController {

    private final ObjectMapper objectMapper;
    private final FileOperationsService fileOperationsService;

    @PostMapping("/convert-to-xml")
    public ResponseEntity<byte[]> convertToXml(@RequestBody String quizString) throws Exception {
        Quiz quiz = objectMapper.readValue(quizString, Quiz.class);
        return fileOperationsService.convertToXml(quiz);
    }

    @PostMapping("/convert-to-json")
    public ResponseEntity<byte[]> convertToJson(@RequestBody String quizString) throws Exception {
        Quiz quiz = objectMapper.readValue(quizString, Quiz.class);
        return fileOperationsService.convertToJson(quiz);
    }

    @PostMapping("/convert-to-pdf")
    public ResponseEntity<byte[]> convertToPdf(@RequestBody String quizString) throws Exception {
        Quiz quiz = objectMapper.readValue(quizString, Quiz.class);
        return fileOperationsService.convertToPdf(quiz);
    }

    @PostMapping("/upload-and-convert")
    public ResponseEntity<String> uploadAndConvertFile(@RequestPart("file") MultipartFile file) throws Exception {
        return fileOperationsService.uploadAndConvertFile(file);
    }
}