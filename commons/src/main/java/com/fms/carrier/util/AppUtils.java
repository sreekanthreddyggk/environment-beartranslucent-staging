package com.fms.carrier.util;

import java.util.Date;
import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppUtils {
	
	private static String driverPath;
	
	private static WebDriver singletonDriverInstance = null;
	
	@Value("${driver.path}")
    public void setDriverPath(String path) {
		driverPath = path;
    }

	public static Timestamp convertStringToDate(String dateText, String[] dateFormats) {
		for(String format: dateFormats){
			try{
				SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
				Date date = (Date) dateFormatter.parse(dateText);
				return new Timestamp(date.getTime());
			}catch(ParseException e){
				continue;
			}
		}
		return null;
	}
	
	public static WebDriver getDriverInstance(String carrierUrl){
		if(singletonDriverInstance == null){
			File file = new File(driverPath);
			System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
			
			ChromeOptions options = new ChromeOptions();
			options.addArguments("headless");
	        options.addArguments("--proxy-server='direct://'");
	        options.addArguments("--proxy-bypass-list=*");
	        
	        singletonDriverInstance = new ChromeDriver(options);
	        singletonDriverInstance.get(carrierUrl);
		}
        return singletonDriverInstance;
	}
}
