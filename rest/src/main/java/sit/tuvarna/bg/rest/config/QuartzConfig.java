package sit.tuvarna.bg.rest.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sit.tuvarna.bg.rest.job.DailyQuizJob;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail dailyQuizJobDetail() {
        return JobBuilder.newJob(DailyQuizJob.class)
                .withIdentity("dailyQuizJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailyQuizJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(dailyQuizJobDetail())
                .withIdentity("dailyQuizJobTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0)
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}
