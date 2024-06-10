package sit.tuvarna.bg.core.externalservices;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.properties.ListNumberingType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import sit.tuvarna.bg.persistence.entity.Answer;
import sit.tuvarna.bg.persistence.entity.Question;
import sit.tuvarna.bg.persistence.entity.Quiz;

@Service
@RequiredArgsConstructor
public class FileOperationsService {
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;
    private static final Logger logger = LoggerFactory.getLogger(FileOperationsService.class);


    public ResponseEntity<byte[]> convertToXml(Quiz quiz) throws Exception {
        String xmlContent = xmlMapper.writeValueAsString(quiz); // Serialize to pretty XML
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        String encodedFilename = URLEncoder.encode(quiz.getTitle() + ".xml", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", encodedFilename);

        return new ResponseEntity<>(xmlContent.getBytes(StandardCharsets.UTF_8), headers, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> convertToJson(Quiz quiz) throws Exception {
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(quiz); // Serialize to pretty JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedFilename = URLEncoder.encode(quiz.getTitle() + ".json", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", encodedFilename);

        return new ResponseEntity<>(jsonContent.getBytes(StandardCharsets.UTF_8), headers, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> convertToPdf(Quiz quiz) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        InputStream fontStream = getClass().getResourceAsStream("/fonts/FreeSans.ttf");
        byte[] fontBytes = IOUtils.toByteArray(fontStream);
        PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        document.add(new Paragraph("Име: ________").setFont(font));
        document.add(new Paragraph("Фамилия: ________").setFont(font));
        document.add(new Paragraph("\n").setFont(font));

        document.add(new Paragraph("Заглавие: " + quiz.getTitle()).setFont(font));
        document.add(new Paragraph("Категория: " + quiz.getCategory().getCategory()).setFont(font));
        document.add(new Paragraph("\nВъпроси:\n").setFont(font));

        int questionNumber = 1;
        for (Question question : quiz.getQuestions()) {
            String questionText = "Въпрос " + questionNumber + ". " + question.getQuestion();
            if (question.getType().name().equals("MULTIPLE_ANSWER")) {
                questionText += " (Няколко верни отговора)";
            }
            Paragraph questionParagraph = new Paragraph("\n" + questionText).setFont(font);
            document.add(questionParagraph);

            List list = new List(ListNumberingType.DECIMAL);

            if (question.getType().name().equals("OPEN")) {
                document.add(new Paragraph("_________________________________________").setFont(font));
            } else {
                char answerOption = 'a';
                for (Answer answer : question.getAnswers()) {
                    document.add(new Paragraph(answerOption + ") " + answer.getContent()).setFont(font));
                    answerOption++;
                }
            }

            document.add(new Paragraph().add(list).setKeepTogether(true));
            questionNumber++;
        }

        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String encodedFilename = URLEncoder.encode(quiz.getTitle() + ".pdf", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", encodedFilename);

        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

    public ResponseEntity<String> uploadAndConvertFile(MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            Quiz quiz;
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".json")) {
                quiz = objectMapper.readValue(content, Quiz.class);
            } else if (file.getOriginalFilename().endsWith(".xml")) {
                quiz = xmlMapper.readValue(content, Quiz.class);
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }

            String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(quiz);
            return ResponseEntity.ok(jsonContent);
        } catch (Exception e) {
            logger.error("Error while uploading and converting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the file: " + e.getMessage());
        }
    }
}
