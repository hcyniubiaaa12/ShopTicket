package com.shop.config;

import com.shop.enums.OrderStatus;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.print.attribute.standard.JobName;

@Configuration
public class Quartz {
    @Autowired
    private Scheduler scheduler;

    public void createJob(String jobName, Class jobClass, String cron ) {
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobName)
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "_trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .forJob(jobName)
                .build();
        try {
            scheduler.scheduleJob(job, trigger);
            System.out.println("✅ 任务 [" + jobName + "] 已创建，Cron: " + cron);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }
    public  void removeJob(String jobName){
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.deleteJob(jobKey);
            System.out.println("✅ 任务 [" + jobName + "] 已删除");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
