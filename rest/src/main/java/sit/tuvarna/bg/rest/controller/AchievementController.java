package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementOperation;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementRequest;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementResponse;

@RestController
@RequestMapping(path = "/api/v1/achievement")
public class AchievementController {

    private final CreateAchievementOperation createAchievement;

    @Autowired
    public AchievementController(CreateAchievementOperation createAchievement) {
        this.createAchievement = createAchievement;
    }

    @PostMapping
    public ResponseEntity<CreateAchievementResponse> create(@RequestBody @Valid CreateAchievementRequest request) {
        return new ResponseEntity<>(createAchievement.process(request), HttpStatus.CREATED);
    }
}
