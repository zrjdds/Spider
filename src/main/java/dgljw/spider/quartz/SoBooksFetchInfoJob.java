package dgljw.spider.quartz;

import java.util.ArrayList;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dgljw.spider.app.SoBooksSpider;
import dgljw.spider.processor.SoBooksPageProcessor;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

public class SoBooksFetchInfoJob implements Job {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println("I AM SoBooksFetchInfoJob");
		
		SoBooksSpider spider = null;

		if (SoBooksSpider.CheckProxy() == true) {
			HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
			httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("cmproxy.gmcc.net", 8081)));

			SoBooksPageProcessor soBooksPageProcessor = new SoBooksPageProcessor();
			soBooksPageProcessor.hasProxy = true;

			spider = SoBooksSpider.create(soBooksPageProcessor);
			spider.hasProxy = true;
			spider.setDownloader(httpClientDownloader);
			spider.thread(1);
		} else {
			SoBooksPageProcessor soBooksPageProcessor = new SoBooksPageProcessor();
			soBooksPageProcessor.hasProxy = false;
			spider = SoBooksSpider.create(soBooksPageProcessor);
			spider.hasProxy = false;
			spider.thread(1);
		}

		ArrayList<String> URLs = new ArrayList<String>();
		for (int i = 1; i <= 1; i++) {
			String URL = "https://sobooks.cc/page/" + i;
			URLs.add(URL);
			spider.addUrl(URLs);
		}

		spider.start();
	}

}
