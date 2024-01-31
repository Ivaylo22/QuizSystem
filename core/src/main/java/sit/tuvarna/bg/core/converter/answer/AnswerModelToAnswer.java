package sit.tuvarna.bg.core.converter.answer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.persistence.entity.Answer;

@Component
public class AnswerModelToAnswer implements Converter<AnswerModel, Answer> {
    @Override
    public Answer convert(AnswerModel source) {
        return Answer.builder()
                .content(source.getContent())
                .correct(source.getIsCorrect())
                .build();
    }
}
