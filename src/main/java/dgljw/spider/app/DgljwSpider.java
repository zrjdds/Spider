package dgljw.spider.app;


import org.quartz.SchedulerException;

import dgljw.spider.quartz.SoBooksConfigue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DgljwSpider {
	
	private static final Logger logger = LoggerFactory.getLogger(DgljwSpider.class);
		
	public static void main(String[] args) {
				
		logger.info("Application start");
		
		String jdkVersion = System.getProperty("java.version");
		if (jdkVersion.contains("1.8") == false) {
			System.out.println("请使用JDK 1.8执行");
			return;
		}
		try {
			(new SoBooksConfigue()).configue();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		
 	}
}
