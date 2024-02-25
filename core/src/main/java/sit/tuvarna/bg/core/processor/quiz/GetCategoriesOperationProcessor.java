package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesOperation;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesRequest;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesResponse;
import sit.tuvarna.bg.persistence.entity.Category;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCategoriesOperationProcessor implements GetCategoriesOperation {
    private final CategoryRepository categoryRepository;

    @Override
    public GetCategoriesResponse process(GetCategoriesRequest request) {
        List<Category> categories = categoryRepository.findAll();
        return GetCategoriesResponse.builder()
                .categories(categories.stream().map(Category::getCategory).toList())
                .build();
    }
}
