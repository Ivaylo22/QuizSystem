package controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementOperation;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementRequest;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementResponse;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsOperation;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsRequest;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsResponse;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsOperation;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsRequest;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import sit.tuvarna.bg.rest.QuizApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QuizApplication.class)
@AutoConfigureMockMvc
public class AchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAchievementOperation createAchievement;

    @MockBean
    private ListAchievementsOperation listAchievement;

    @MockBean
    private ListEarnedAchievementsOperation listEarnedAchievements;

    private static final String LIST_ACHIEVEMENTS = "/api/v1/achievement/list";
    private static final String LIST_EARNED_ACHIEVEMENTS = "/api/v1/achievement/list-earned";
    private static final String CREATE_ACHIEVEMENT = "/api/v1/achievement/create";

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(createAchievement, listAchievement, listEarnedAchievements);
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // Allow empty beans
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testListAchievements() throws Exception {
        ListAchievementsResponse response = new ListAchievementsResponse();
        ListAchievementsRequest request = ListAchievementsRequest.builder().build();
        when(listAchievement.process(any(ListAchievementsRequest.class))).thenReturn(response);

        mockMvc.perform(get(LIST_ACHIEVEMENTS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testListEarnedAchievements() throws Exception {
        ListEarnedAchievementsResponse response = new ListEarnedAchievementsResponse();
        ListEarnedAchievementsRequest request = ListEarnedAchievementsRequest.builder()
                .userEmail("test@example.com")
                .build();
        when(listEarnedAchievements.process(any(ListEarnedAchievementsRequest.class))).thenReturn(response);

        mockMvc.perform(get(LIST_EARNED_ACHIEVEMENTS)
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testCreateAchievement() throws Exception {
        CreateAchievementRequest request = CreateAchievementRequest.builder()
                .name("Test Achievement")
                .description("This is a test achievement")
                .achievementPoints(10)
                .build();
        CreateAchievementResponse response = new CreateAchievementResponse();
        when(createAchievement.process(any(CreateAchievementRequest.class))).thenReturn(response);

        mockMvc.perform(post(CREATE_ACHIEVEMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
