package sit.tuvarna.bg.rest.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.core.scheduling.DailyQuizScheduling;

@Component
@RequiredArgsConstructor
public class DailyQuizJob implements Job {

    private final DailyQuizScheduling quizService;

    @Override
    public void execute(JobExecutionContext context) {
        quizService.checkAndUpdateDailyQuiz();
    }
}
