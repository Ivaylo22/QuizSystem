package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsOperation;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsRequest;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsResponse;
import sit.tuvarna.bg.persistence.entity.Subject;
import sit.tuvarna.bg.persistence.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSubjectsOperationProcessor implements GetSubjectsOperation {
    private final SubjectRepository subjectRepository;

    @Override
    public GetSubjectsResponse process(GetSubjectsRequest request) {
        List<Subject> subjects = subjectRepository.findAll();
        return GetSubjectsResponse.builder()
                .subjects(subjects.stream()
                        .map(Subject::getSubject)
                        .toList())
                .build();
    }
}
