package sit.tuvarna.bg.core.converter.test;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.model.TestModel;
import sit.tuvarna.bg.persistence.entity.Test;

@Component
public class TestToTestModel implements Converter<Test, TestModel> {
    @Override
    public TestModel convert(Test source) {
        return TestModel.builder()
                .id(source.getId().toString())
                .title(source.getTitle())
                .grade(source.getGrade())
                .subject(source.getSubject().getSubject())
                .creatorEmail(source.getCreatorEmail())
                .minutesToSolve(source.getMinutesToSolve())
                .build();
    }
}
