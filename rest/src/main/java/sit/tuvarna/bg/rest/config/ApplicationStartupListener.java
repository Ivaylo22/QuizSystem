package sit.tuvarna.bg.rest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.core.scheduling.DailyQuizScheduling;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final DailyQuizScheduling quizService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application started, checking and updating daily quiz if necessary.");
        quizService.checkAndUpdateDailyQuiz();
    }
}
