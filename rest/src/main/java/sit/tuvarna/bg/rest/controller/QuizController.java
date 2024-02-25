package sit.tuvarna.bg.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesOperation;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/quiz")
public class QuizController {
    private final GetCategoriesOperation getCategories;


}
