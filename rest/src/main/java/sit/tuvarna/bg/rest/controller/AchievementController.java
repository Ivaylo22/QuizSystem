package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementOperation;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementRequest;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementResponse;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsOperation;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsRequest;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsResponse;

@RestController
@RequestMapping(path = "/api/v1/achievement")
public class AchievementController {
    private final CreateAchievementOperation createAchievement;
    private final ListAchievementsOperation listAchievement;

    @Autowired
    public AchievementController(CreateAchievementOperation createAchievement,
                                 ListAchievementsOperation listAchievement) {
        this.createAchievement = createAchievement;
        this.listAchievement = listAchievement;
    }

    @GetMapping("/list")
    public ResponseEntity<ListAchievementsResponse> listAchievements(@RequestBody @Valid ListAchievementsRequest request) {
        return new ResponseEntity<>(listAchievement.process(request), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateAchievementResponse> create(@RequestBody @Valid CreateAchievementRequest request) {
        return new ResponseEntity<>(createAchievement.process(request), HttpStatus.CREATED);
    }
}
