package com.fms.carrier.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fms.carrier.constants.AppConstants;
import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.dto.SourceTableDTO;
import com.fms.carrier.enums.StatusPattern;
import com.fms.carrier.util.AppUtils;

@Service
public class AclCrawlerService {
	
	@Autowired
	private CarrierDAO carrierDAO;
	
	@Autowired
	private CarrierService carrierService;
	
	@Value("${carrier.url}")
	private String carrierUrl;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AclCrawlerService.class);
	
	/*
	 * Functionality to load,crawl the webpage and save status of container in output table
	 */
	public void crawl(String carrierCode, List<SourceTableDTO> inputList){
		LOGGER.info("Started crawling for carrier: "+carrierCode);
		try{
			WebDriver driver = AppUtils.getDriverInstance(carrierUrl);
			LOGGER.info("Web driver instantiated for carrier: "+carrierCode);
			if(driver.findElement(By.xpath("/html/body/div/div/a")).isDisplayed()){
				driver.findElement(By.xpath("/html/body/div/div/a")).click();
			}
			LOGGER.info("Loaded webpage for carrier: "+carrierCode);
			for(SourceTableDTO input: inputList){
				String status = AppConstants.SUCCESS;
				try{
					Document page = getPageDocument(driver, input.getBookingRef());
					
					String errorElement = "table table table table table table tbody tr td:nth-of-type(2) span.subheader";
					if(page.select(errorElement).size() > 0){
						LOGGER.info("Entered booking reference-"+input.getBookingRef()+" is not valid");
						page = getPageDocument(driver, input.getBlNumber());
						
						if(page.select(errorElement).size() > 0){
							LOGGER.info("Entered bl number-"+input.getBlNumber()+" is not valid");
							page = getPageDocument(driver, input.getContainerno());
							if(page.select(errorElement).size() > 0){
								status = AppConstants.NOT_FOUND;
								LOGGER.info("Entered container number-"+input.getContainerno()+" is not valid");
							} else{
								LOGGER.info("Crawling started for container :"+input.getContainerno());
								crawlContainer(driver, page, carrierCode, "", "");
								LOGGER.info("Crawling completed for container :"+input.getContainerno());
							}
						} else{
							LOGGER.info("Crawling started for bl number :"+input.getBlNumber());
							crawlContainer(driver, page, carrierCode, "", input.getBlNumber());
							LOGGER.info("Crawling completed for bl number :"+input.getBlNumber());
						}
					} else{
						LOGGER.info("Crawling started for booking reference :"+input.getBookingRef());
						crawlContainer(driver, page, carrierCode, input.getBookingRef() , input.getBlNumber());
						LOGGER.info("Crawling completed for booking reference :"+input.getBookingRef());
						
					}
					carrierDAO.updateInputProcessingTimeAndStatus(input.getCarrier(), input.getContainerno(), input.getBookingRef(), 
							input.getBlNumber(), status, new Timestamp(new Date().getTime()));
				}catch(Exception e){
					LOGGER.error(e.getMessage());
					carrierDAO.updateInputProcessingTimeAndStatus(input.getCarrier(), input.getContainerno(), input.getBookingRef(), 
							input.getBlNumber(), AppConstants.ERROR, new Timestamp(new Date().getTime()));
				}
			}
		}catch(Exception e){
			LOGGER.error(e.getMessage());
		}finally{
			LOGGER.info("Web Crawling Completed for carrier: "+ carrierCode);
		}
	}
	
	private Document getPageDocument(WebDriver driver, String input){
		driver.findElement(By.cssSelector("form[name='track_cargo'] textarea")).clear();
		driver.findElement(By.cssSelector("form[name='track_cargo'] textarea")).sendKeys(input);
		driver.findElement(By.cssSelector("form[name='track_cargo'] input[type='submit']")).click();
		return Jsoup.parse(driver.getPageSource());
	}
	
	private void crawlContainer(WebDriver driver, Document page, String carrierCode, String bookingRef, String blNumber) throws Exception{
		String statusVoyage = page.select("table table table table table table table table:nth-of-type(1) tbody tr:nth-of-type(3) td:first-child").text().split(":")[1].trim();
		
		int noOfContainers = driver.findElements(By.cssSelector("table table table table table table table table")).size();
		
		for(int index=1; index<=noOfContainers; index++){
			driver.findElement(By.cssSelector("table table table table table table table table:nth-of-type("+index+") tbody tr:nth-of-type(4) td:first-child input")).click();
			Document doc = Jsoup.parse(driver.getPageSource());
			
			String containerNo = doc.select("table table table table table table tbody tr td:nth-of-type(2) span.subheader").text().split(":")[1].trim();
			Elements data = doc.select("table table table table table table tbody tr td:nth-of-type(2) div.gray");
			for(Element div : data){
			     String text = div.text();
			     
			     if(!text.trim().equals("")){
			    	 for(StatusPattern pattern: StatusPattern.values()){
			    		 String[] dateFormats = {AppConstants.ACL_DATE_FORMAT};
			    		 Matcher m = pattern.getPattern().matcher(text);
			    		 if(m.matches() && pattern.name().matches("p1|p2|p3")){
			    			 carrierService.saveContainerStatus(carrierCode, containerNo, bookingRef, blNumber, m.group(1),
			    					 AppUtils.convertStringToDate(m.group(4), dateFormats), 
			    					 m.group(3), m.group(2), statusVoyage);
			    			 break;
			    		 } else if(m.matches() && pattern.name().matches("p5|p6|p7")){
			    			 carrierService.saveContainerStatus(carrierCode, containerNo, bookingRef, blNumber, m.group(1),
			    					 AppUtils.convertStringToDate(m.group(3), dateFormats),
			    					 m.group(2), "", statusVoyage);
			    			 break;
			    		 } else if(m.matches() && pattern.name().matches("p4")){
			    			 carrierService.saveContainerStatus(carrierCode, containerNo, bookingRef, blNumber, m.group(1),
			    					 AppUtils.convertStringToDate(m.group(4), dateFormats),
			    					 m.group(2), m.group(3), statusVoyage);
			    			 break;
			    		 }
			    	 }
			     }
			}
			LOGGER.info("Status saved for container: "+containerNo);
			driver.navigate().back();
		}
	}
	
}
