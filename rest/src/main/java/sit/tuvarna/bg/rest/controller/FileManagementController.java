package sit.tuvarna.bg.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.tuvarna.bg.core.externalservices.FileOperationsService;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.Test;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileManagementController {

    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;
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

    @PostMapping("/convert-test-to-xml")
    public ResponseEntity<byte[]> convertTestToXml(@RequestBody String testString) throws Exception {
        Test test = objectMapper.readValue(testString, Test.class);
        return fileOperationsService.convertTestToXml(test);
    }

    @PostMapping("/convert-test-to-json")
    public ResponseEntity<byte[]> convertTestToJson(@RequestBody String testString) throws Exception {
        Test test = objectMapper.readValue(testString, Test.class);
        return fileOperationsService.convertTestToJson(test);
    }

    @PostMapping("/convert-test-to-pdf")
    public ResponseEntity<byte[]> convertTestToPdf(@RequestBody String requestBody) {
        try {
            JsonNode requestJson = objectMapper.readTree(requestBody);
            String testString = requestJson.get("testString").asText();
            int variants = requestJson.get("variants").asInt();

            Test test = objectMapper.readValue(testString, Test.class);
            return fileOperationsService.convertTestToPdf(test, variants);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }

    @PostMapping("/upload-and-convert-test")
    public ResponseEntity<String> uploadAndConvertTest(@RequestPart("file") MultipartFile file) throws Exception {
        return fileOperationsService.uploadAndConvertTestFile(file);
    }
}
