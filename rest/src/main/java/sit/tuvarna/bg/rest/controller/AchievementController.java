package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsOperation;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsRequest;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsResponse;

@RestController
@RequestMapping(path = "/api/v1/achievement")
public class AchievementController {
    private final CreateAchievementOperation createAchievement;
    private final ListAchievementsOperation listAchievement;
    private final ListEarnedAchievementsOperation listEarnedAchievements;

    @Autowired
    public AchievementController(CreateAchievementOperation createAchievement,
                                 ListAchievementsOperation listAchievement,
                                 ListEarnedAchievementsOperation listEarnedAchievements) {
        this.createAchievement = createAchievement;
        this.listAchievement = listAchievement;
        this.listEarnedAchievements = listEarnedAchievements;
    }

    @GetMapping("/list")
    public ResponseEntity<ListAchievementsResponse> listAchievements() {
        ListAchievementsRequest request = new ListAchievementsRequest();
        return new ResponseEntity<>(listAchievement.process(request), HttpStatus.OK);
    }

    @GetMapping("/list-earned")
    public ResponseEntity<ListEarnedAchievementsResponse> listEarnedAchievements(
            @RequestParam @NotBlank(message = "User email is required") String email) {
        ListEarnedAchievementsRequest request = ListEarnedAchievementsRequest
                .builder()
                .userEmail(email)
                .build();
        return new ResponseEntity<>(listEarnedAchievements.process(request), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateAchievementResponse> create(@RequestBody @Valid CreateAchievementRequest request) {
        return new ResponseEntity<>(createAchievement.process(request), HttpStatus.CREATED);
    }
}
