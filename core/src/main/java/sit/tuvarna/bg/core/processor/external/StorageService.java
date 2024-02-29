package sit.tuvarna.bg.core.processor.external;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sit.tuvarna.bg.api.exception.QuestionNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.persistence.entity.Question;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.QuestionRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private AmazonS3 s3Client;

    @Value("${access-key}")
    private String accessKey;

    @Value("${secret-access-key}")
    private String secretAccessKey;

    @Value("${bucket-name}")
    private String bucketName;

    private final UserRepository userRepository;

    private final QuestionRepository questionRepository;

    @PostConstruct
    private void initializeAmazon() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretAccessKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("eu-north-1")
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public String uploadUserAvatar(MultipartFile file, String userEmail) throws IOException {
        String fileKey = getFileKey(file);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);
        user.setAvatarUrl(s3Client.getUrl(bucketName, fileKey).toString());
        userRepository.save(user);

        return s3Client.getUrl(bucketName, fileKey).toString();
    }

    public String uploadQuestionImage(MultipartFile file, String questionId) throws IOException {
        String fileKey = getFileKey(file);

        Question question = questionRepository.findById(UUID.fromString(questionId))
                .orElseThrow(QuestionNotFoundException::new);

        question.setImage(s3Client.getUrl(bucketName, fileKey).toString());
        questionRepository.save(question);

        return s3Client.getUrl(bucketName, fileKey).toString();
    }

    private String getFileKey(MultipartFile file) throws IOException {
        String fileKey = UUID.randomUUID().toString();

        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata));

        return fileKey;
    }
}
