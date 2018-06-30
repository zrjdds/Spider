package dgljw.spider.app;


import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import dgljw.spider.pojo.SobooksBook;
import dgljw.spider.dbhelper.MySQLHelper;
import dgljw.spider.processor.SoBooksPageProcessor;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

public class SoBooksSpider extends us.codecraft.webmagic.Spider {

	public boolean hasProxy = false;

	public SoBooksSpider(PageProcessor pageProcessor) {
		super(pageProcessor);
	}

	public static SoBooksSpider create(PageProcessor pageProcessor) {
		return new SoBooksSpider(pageProcessor);
	}

	public void DownLoadBooks() {

		java.sql.Connection conn = MySQLHelper.getConnection("com.mysql.jdbc.Driver",
				"jdbc:mysql://10.25.0.141:3306/TECHBLOGS?characterEncoding=utf-8", "root", "Workhard_1234");
		String sql = "SELECT * FROM SOBOOKSBOOK WHERE BOOKDOWNLOADED = 1";
		ResultSet result = MySQLHelper.executQuery(conn, sql, null);
		
		ArrayList<SobooksBook> sobooksBookList = new ArrayList<SobooksBook>();

		try {
			while (result.next()) {
				
				SobooksBook newBook = new SobooksBook();
				newBook.setBookTitle(result.getString(3));
				newBook.setBookTitle(result.getString(4));
				newBook.setBookBaiduLink(result.getString(6));
				newBook.setBookBaiduPassword(result.getString(7));
				
				sobooksBookList.add(newBook);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (this.pageProcessor instanceof SoBooksPageProcessor) {

			SoBooksPageProcessor pageProcessor = (SoBooksPageProcessor) (this.pageProcessor);
			
			for (int i = 0; i < sobooksBookList.size(); i++) {
				SobooksBook book = (SobooksBook) (sobooksBookList.get(i));

				try {
					Thread.sleep(1000);
					WebDriver driver = pageProcessor.getChromeDriver();

					driver.get(book.getBookBaiduLink());

					driver.findElement(By.xpath(
							"//*[@class='docs init-docs']/div[@class='acss-header']/div[@class='verify-form']/form[@class='clearfix']/div[@class='verify-input ac-close clearfix']/dl[@class='pickpw clearfix']/dd[@class='clearfix']/input"))
							.sendKeys(book.getBookBaiduPassword());
					driver.findElement(By.xpath(
							"//*[@class='docs init-docs']/div[@class='acss-header']/div[@class='verify-form']/form[@class='clearfix']/div[@class='verify-input ac-close clearfix']/dl[@class='pickpw clearfix']/dd[@class='clearfix']/div/a"))
							.click();

					Thread.sleep(1000);

					driver.findElement(By.xpath(
							"//*[@class='frame-all']/div[@class='frame-main']/div[@class='frame-content']/div[@class='module-share-header']/div[@class='slide-show-header clearfix']/div[@class='slide-show-right']/div[@class='module-share-top-bar g-clearfix']/div/div[@class='x-button-box']/a[2]"))
							.click();
					
					
				} catch (Exception ex) {
					ex.printStackTrace();
					continue;
				}
			}
		}
	}

	@Override
	public void run() {
		checkRunningStat();
		initComponent();
		logger.info("Spider {} started!", getUUID());
		while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
			final Request request = scheduler.poll(this);
			if (request == null) {
				if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
					break;
				}
				// wait until new url added
				waitNewUrl();
			} else {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							processRequest(request);
							onSuccess(request);
						} catch (Exception e) {
							onError(request);
							logger.error("process request " + request + " error", e);
						} finally {
							pageCount.incrementAndGet();
							signalNewUrl();
						}
					}
				});
			}
		}

		this.DeleteDumplicatedBooks();

		stat.set(STAT_STOPPED);
		// release some resources
		if (destroyWhenExit) {
			close();
		}
		logger.info("Spider {} closed! {} pages downloaded.", getUUID(), pageCount.get());

	}

	public SoBooksSpider addUrl(ArrayList<String> urls) {
		for (String url : urls) {
			addRequest(new Request(url));
		}

		signalNewUrl();
		return this;
	}

	public void DeleteDumplicatedBooks() {
		try {
			java.sql.Connection conn = MySQLHelper.getConnection("com.mysql.jdbc.Driver",
					"jdbc:mysql://10.25.0.141:3306/TECHBLOGS?characterEncoding=utf-8", "root", "Workhard_1234");

			String sql = "DROP TABLE IF EXISTS SOBOOKSBOOKBAK";
			MySQLHelper.executUpdate(conn, sql);

			conn = MySQLHelper.getConnection("com.mysql.jdbc.Driver",
					"jdbc:mysql://10.25.0.141:3306/TECHBLOGS?characterEncoding=utf-8", "root", "Workhard_1234");

			sql = "CREATE TABLE SOBOOKSBOOKBAK AS SELECT * FROM SOBOOKSBOOK";
			MySQLHelper.executUpdate(conn, sql);

			conn = MySQLHelper.getConnection("com.mysql.jdbc.Driver",
					"jdbc:mysql://10.25.0.141:3306/TECHBLOGS?characterEncoding=utf-8", "root", "Workhard_1234");

			sql = "DELETE FROM SOBOOKSBOOK WHERE BOOKTITLE IN (SELECT BOOKTITLE FROM SOBOOKSBOOKBAK GROUP BY BOOKTITLE HAVING COUNT(*) > 1 ) AND SAMPLETIME NOT IN (SELECT MIN(SAMPLETIME) FROM SOBOOKSBOOKBAK GROUP BY BOOKTITLE HAVING COUNT(*) > 1)";
			MySQLHelper.executUpdate(conn, sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public static boolean CheckProxy() {
		try {
			java.net.URL url = new java.net.URL("http://www.baidu.com");
			InetSocketAddress addr = null;
			addr = new InetSocketAddress("cmproxy.gmcc.net", 8081);
			java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, addr); // http 代理
			URLConnection conn;
			conn = url.openConnection(proxy);
			InputStream in = conn.getInputStream();
			String s = IOUtils.toString(in);
			if (s.indexOf("百度") > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}
}