package sit.tuvarna.bg.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final FileOperationsService fileOperationsService;
    private static final Logger logger = LoggerFactory.getLogger(FileManagementController.class);

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
    public ResponseEntity<byte[]> convertTestToXml(@RequestBody String testString) {
        try {
            // Deserialize the input string into a Test object using ObjectMapper
            Test test = objectMapper.readValue(testString, Test.class);

            // Convert the Test object to XML and return the response
            return fileOperationsService.convertTestToXml(test);
        } catch (Exception e) {
            // Log the exception using a logger
            logger.error("Error while converting test to XML", e);

            // Return an error response with appropriate status code and message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error while processing the request: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
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
    public ResponseEntity<String> uploadAndConvertTestFile(@RequestParam("file") MultipartFile file) {
        try {
            String jsonContent = fileOperationsService.uploadAndConvertTestFile(file);
            return ResponseEntity.ok(jsonContent);
        } catch (Exception e) {
            logger.error("Error while uploading and converting test file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the file: " + e.getMessage());
        }
    }
}
