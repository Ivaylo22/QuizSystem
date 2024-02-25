package sit.tuvarna.bg.core.converter.quiz;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.persistence.entity.Quiz;

@Component
public class QuizToQuizModel implements Converter<Quiz, QuizModel> {
    @Override
    public QuizModel convert(Quiz source) {
        return QuizModel
                .builder()
                .name(source.getTitle())
                .category(source.getCategory().getCategory())
                .build();
    }
}
