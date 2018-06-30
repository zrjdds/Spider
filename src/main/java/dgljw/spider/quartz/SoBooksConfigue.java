package dgljw.spider.quartz;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dgljw.spider.app.DgljwSpider;

public class SoBooksConfigue {
	
	private static final Logger logger = LoggerFactory.getLogger(SoBooksConfigue.class);

	public void configue() throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = sf.getScheduler();
		
		long time = System.currentTimeMillis() + 3 * 1000L; 
		Date statTime = new Date(time);

		JobDetail job1 = JobBuilder.newJob(SoBooksFetchInfoJob.class)
				.withDescription("SoBooksFechInfoJob") 
				.withIdentity("SoBooksFechInfoJob", "SoBooksJobGroup")  
				.build();

		Trigger trigger1 = TriggerBuilder.newTrigger().
				withDescription("").
				withIdentity("SoBooksFechInfoTrigger", "SoBooksTriggerGroup")
				.startAt(statTime)  
				.withSchedule(CronScheduleBuilder.cronSchedule("0 * 13 * * ?")) // 每两小时执行一次
				.build();

		JobDetail job2 = JobBuilder.newJob(SoBooksDownloadJob.class)
				.withDescription("SoBooksDownloadJob") 
				.withIdentity("SoBooksDownloadJob", "SoBooksJobGroup")  
				.build();
		
		Trigger trigger2 = TriggerBuilder.newTrigger().
				withDescription("").
				withIdentity("SoBookDownloadTrigger", "SoBooksTriggerGroup")
				.startAt(statTime)  
				.withSchedule(CronScheduleBuilder.cronSchedule("5 34 * * * ?")) // 每两小时执行一次
				.build();
		
		scheduler.scheduleJob(job1, trigger1);
		//scheduler.scheduleJob(job2, trigger2);
		
		scheduler.start();
		
		System.out.println("fff");
	}

}
