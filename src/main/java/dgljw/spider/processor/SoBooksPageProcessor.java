package dgljw.spider.processor;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;

import dgljw.spider.pojo.*;
import dgljw.spider.dbhelper.MySQLHelper;
import dgljw.spider.app.SoBooksSpider;

public class SoBooksPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(3000).setUserAgent(
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");

	public ArrayList<SobooksBook> sobooksBookList = new ArrayList<SobooksBook>();
	public boolean hasProxy = true;

	// chrome
	// driver跟电脑上的chrome浏览器要有一个版本映射关系，http://chromedriver.storage.googleapis.com/index.html
	public String chromeDriverPath = "C:\\chromedriver.exe";

	public SoBooksPageProcessor() {
		System.setProperty("webdriver.chrome.driver", this.chromeDriverPath);
	}

	private boolean SaveBookToDB(SobooksBook book) {

		java.sql.Connection conn = MySQLHelper.getConnection("com.mysql.jdbc.Driver",
				"jdbc:mysql://10.25.0.141:3306/TECHBLOGS?characterEncoding=utf-8", "root", "Workhard_1234");

		/*
		 * SoBook表
		 * 
		 * CREATE DATABASE TECHBLOGS DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
		 * 
		 * CREATE TABLE SOBOOKSBOOK ( SAMPLETIME DATETIME, BOOKIMGURL VARCHAR(2000),
		 * BOOKTITLE VARCHAR(1000), BOOKAUTHOR VARCHAR(100), BOOKFORMAT VARCHAR(100),
		 * BOOKBAIDULINK VARCHAR(4000), BOOKBAIDUPASSWORD VARCHAR(100),
		 * BOOKCONTENTDESCRIPTION VARCHAR(4000), BOOKAUTHORDESCRIPTION VARCHAR(4000),
		 * BOOKFILENAME VARCHAR(1000), BOOKFILEPATH VARCHAR(1000), BOOKSOURCE
		 * VARCHAR(4000), BOOKDOWNLOADED INT(4)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
		 */

		java.util.Date date = new java.util.Date();
		java.sql.Timestamp tt = new java.sql.Timestamp(date.getTime());

		String sql = "INSERT INTO SOBOOKSBOOK VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] param = { tt, book.getBookImgURL().toString(), book.getBookTitle().toString(),
				book.getBookAuthor().toString(), book.getBookFormat().toString(), book.getBookBaiduLink().toString(),
				book.getBookBaiduPassword().toString(), book.getBookContentDescription().toString(),
				book.getBookAuthorDescription().toString(), book.getBookFileName().toString(),
				book.getBookFilePath().toString(), book.getBookSource().toString(), 0 };

		MySQLHelper.executUpdate(conn, sql, param);

		/*
		 * String sql = "INSERT INTO SOBOOKSBOOK VALUES('"+ book.getBookImgURL() + "','"
		 * + book.getBookTitle() + "','" + book.getBookAuthor() + "','" +
		 * book.getBookFormat() + "','" + book.getBookBaiduLink() + "','" +
		 * book.getBookBaiduPassword() + "','" + book.getBookContentDescription() +
		 * "','" + book.getBookAuthorDescription() + "','" + book.getBookFileName() +
		 * "','" + book.getBookFilePath() + "','" + book.getBookSource() + "','" + "0" +
		 * "')";
		 * 
		 * 
		 * MySQLHelper.executUpdate(conn, sql);
		 */

		return true;
	}
 
	
	public ChromeDriver getChromeDriver() {
		
	
		if (this.hasProxy == true) {
			String proxyIpAndPort = "cmproxy.gmcc.net:8081";
			DesiredCapabilities cap = new DesiredCapabilities();
			org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
			proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
			cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
			cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
			// System.setProperty("http.nonProxyHosts", "localhost");
			cap.setCapability(CapabilityType.PROXY, proxy);
			
			//return new ChromeDriver(cap);
			
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--lang=" + "zh-CN");
			chromeOptions.addArguments("--proxy-server=http://cmproxy.gmcc.net:8081");
			chromeOptions.addArguments("download.default_directory", "C:\\Downloads");
			
			String downloadFilepath = "D:\\";
		    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		    chromePrefs.put("profile.default_content_settings.popups", 0);
		    chromePrefs.put("download.default_directory", downloadFilepath);
		    chromeOptions.setExperimentalOption("prefs", chromePrefs);
			
			return new ChromeDriver(chromeOptions);	
		} else {
			
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--lang=" + "zh-CN");
			chromeOptions.addArguments("download.default_directory", "C:\\Downloads");
			
			String downloadFilepath = "D:\\";
		    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		    chromePrefs.put("profile.default_content_settings.popups", 0);
		    chromePrefs.put("download.default_directory", downloadFilepath);
		    chromeOptions.setExperimentalOption("prefs", chromePrefs);
		    
			return new ChromeDriver(chromeOptions);
		}
	}
	

	@Override
	public void process(Page page) {

		String URL = page.getUrl().toString();
		if (page.getUrl().regex("https://sobooks.cc/page/[0-9]").match()) {
			try {
				Random rand = new Random();
				Thread.sleep(rand.nextInt(10) * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			page.addTargetRequests(
					page.getHtml().xpath("//*[@class='card col span_1_of_4']/div[@class='shop-item']/div[@class='thumb-img focus']/a/@href").all());
		} else if (page.getUrl().regex("https://sobooks.cc/books/[0-9]").match()) {

			try {
				String bookImgURL = page.getHtml().xpath("//*[@class='bookpic']/img/@src").get();
				String bookTitle = page.getHtml().xpath("//*[@class='bookpic']/img/@title").get();
				String bookAuthor = page.getHtml().xpath("//*[@class='bookinfo']/ul/li[2]/text()").get();
				String bookFormat = page.getHtml().xpath("//*[@class='bookinfo']/ul/li[3]/text()").get().toLowerCase();
				String bookBaiduLink = page.getHtml().xpath("//*[@class='dltable']/tbody/tr[3]/td/a/@href").get();

				List<String> description = page.getHtml().xpath("//*[@class='article-content']/*/text()").all();

				String bookContentDescription = "";
				String bookAuthorDescription = "";
				boolean bookContentDescriptionMatchFlag = false;
				boolean bookAuthorDescriptionMatchFlag = false;
				for (int i = 0; i < description.size(); i++) {
					String line = (String) (description.get(i)).trim();

					if (line.contains("SoBooks所有电子书均来自网络")) {
						continue;
					}

					if (line.contains("本文链接")) {
						continue;
					}

					if (line.equals("内容简介")) {
						bookContentDescriptionMatchFlag = true;

					} else if (line.equals("作者简介")) {
						bookContentDescriptionMatchFlag = false;
						bookAuthorDescriptionMatchFlag = true;
					} else if (bookContentDescriptionMatchFlag == true) {
						bookContentDescription = bookContentDescription + line;
					} else if (bookAuthorDescriptionMatchFlag == true) {
						bookAuthorDescription = bookAuthorDescription + line;
					} else if (line.length() == 0 && bookContentDescriptionMatchFlag == false) {
						if (bookAuthorDescriptionMatchFlag == false) {
							continue;
						} else {
							bookAuthorDescriptionMatchFlag = false;
							break;
						}
					} else {
						bookAuthorDescriptionMatchFlag = false;
						break;
					}
				}

				String pageURL = page.getUrl().toString();
				WebDriver driver = this.getChromeDriver();
				driver.get(pageURL);

				try {
					Random rand = new Random();
					Thread.sleep(rand.nextInt(50) * 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				driver.findElement(By.className("euc-y-i")).sendKeys("201818");
				driver.findElement(By.className("euc-y-s")).submit();

				String bookBaiduPassword = driver.findElement(By.xpath("//*[@class='e-secret']")).getText();
				bookBaiduPassword = bookBaiduPassword.substring(5, bookBaiduPassword.length());

				bookBaiduLink = bookBaiduLink.substring(bookBaiduLink.indexOf("=") + 1, bookBaiduLink.length());

				SobooksBook newBook = new SobooksBook();
				newBook.setBookAuthor(bookAuthor);
				newBook.setBookAuthorDescription(bookAuthorDescription);
				newBook.setBookBaiduLink(bookBaiduLink);
				newBook.setBookBaiduPassword(bookBaiduPassword);
				newBook.setBookContentDescription(bookContentDescription);
				newBook.setBookFileName(bookTitle + "." + bookFormat);
				newBook.setBookFilePath(bookTitle + "." + bookFormat);
				newBook.setBookFormat(bookFormat);
				newBook.setBookImgURL(bookImgURL);
				newBook.setBookTitle(bookTitle);
				newBook.setBookSource(pageURL);
				this.sobooksBookList.add(newBook);

				this.SaveBookToDB(newBook);

				driver.quit();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} else {
			
		}

	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {

	}
}