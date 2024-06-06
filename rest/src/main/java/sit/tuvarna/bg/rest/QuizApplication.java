package sit.tuvarna.bg.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "sit.tuvarna.bg")
@EntityScan(basePackages = "sit.tuvarna.bg.persistence.entity")
@EnableJpaRepositories(basePackages = "sit.tuvarna.bg.persistence.repository")
@EnableMongoRepositories(basePackages = "sit.tuvarna.bg.persistence.repository")
@EnableScheduling
public class QuizApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
    }
}
