package com.fms.carrier.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
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
public class MaeCrawlerService {
	
	@Autowired
	private CarrierDAO carrierDAO;
	
	@Autowired
	private CarrierService carrierService;
	
	@Value("${carrier.url}")
	private String carrierUrl;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MaeCrawlerService.class);

	public void crawl(String carrierCode, List<SourceTableDTO> inputList){
		try{
			WebDriver driver = AppUtils.getDriverInstance(carrierUrl);
			LOGGER.info("Loaded webpage for carrier: "+carrierCode);
			for(SourceTableDTO input: inputList){
				String status = AppConstants.SUCCESS;
				try{
					Document doc = null;
					if(input.getBookingRef() != null && !input.getBookingRef().trim().equals("")){
						driver.get(carrierUrl+input.getBookingRef().trim());
						doc = waitForDomLoad(driver);
					}
					if(doc == null || doc.select(".pt-results .expandable-table").size() == 0){
						LOGGER.info("Entered booking reference-"+input.getBookingRef()+" is not valid");
						doc = null;
						if(input.getBlNumber() != null && !input.getBlNumber().trim().equals("")){
							driver.get(carrierUrl+input.getBlNumber().trim());
							doc = waitForDomLoad(driver);
						}
						
						if(doc == null || doc.select(".pt-results .expandable-table").size() == 0){
							LOGGER.info("Entered bl reference-"+input.getBlNumber()+" is not valid");
							doc = null;
							if(input.getContainerno() != null && !input.getContainerno().trim().equals("")){
								driver.get(carrierUrl+input.getContainerno().trim());
								doc = waitForDomLoad(driver);
							}
							
							if(doc == null || doc.select(".pt-results .expandable-table").size() == 0){
								status = AppConstants.NOT_FOUND;
								LOGGER.info("Entered container number-"+input.getContainerno()+" is not valid");
							}else{
								LOGGER.info("Crawling started for container :"+input.getContainerno());
								crawlContainer(driver, doc, carrierCode, "", "");
								LOGGER.info("Crawling completed for container :"+input.getContainerno());
							}
						}else{
							LOGGER.info("Crawling started for bl number :"+input.getBlNumber());
							crawlContainer(driver, doc, carrierCode, "", input.getBlNumber());
							LOGGER.info("Crawling completed for bl number :"+input.getBlNumber());
						}
					}else{
						LOGGER.info("Crawling started for booking reference :"+input.getBookingRef());
						crawlContainer(driver, doc, carrierCode, input.getBookingRef(), input.getBlNumber());
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
	
	private Document waitForDomLoad(WebDriver driver) throws InterruptedException{
		try {
			  if(driver.findElements(By.className("notification--info")).size() != 0 || 
		                 driver.findElements(By.className("resultError")).size() != 0 ) {
				  WebDriverWait explicitWait = new WebDriverWait(driver, 30);
				  if(driver.findElements(By.className("resultError")).size() != 0){
			    	  explicitWait.until(ExpectedConditions.stalenessOf(driver.findElement(By.className("resultError"))));
			      }
			      new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS)
			      .pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class).until(new ExpectedCondition<Boolean>() {
			            public Boolean apply(WebDriver driver) {
			            	return driver.findElement(By.className("pt-loader")).getCssValue("display").equals("none");
			            }
			       });
			      
			     } else {
			    	 new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS)
			        .pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class).until(new ExpectedCondition<Boolean>() {
			              public Boolean apply(WebDriver driver) {
			               return driver.findElements(By.className("notification--info")).size() != 0 || 
			                 driver.findElements(By.className("resultError")).size() != 0;
			              }
			         });
			      
			     }
			  	return Jsoup.parse(driver.getPageSource());
			  }catch(StaleElementReferenceException|NoSuchElementException|TimeoutException e) {
				  LOGGER.error(e.getMessage());
				  throw e;
			  }
	}
	
	private void crawlContainer(WebDriver driver, Document page, String carrierCode, String bookingRef, String blNumber) throws Exception{
		int noOfContainers = page.select("td.first-element").size();
		for(int containerIndex=0; containerIndex<noOfContainers; containerIndex++){
			String containerNo = page.select("table#table_id tbody:nth-of-type(1) tr td.first-element span:nth-of-type(4)").get(containerIndex).text();
			Element resultTable = page.select("table#table_id tbody:nth-of-type(1) tr td.expandable-table__expand-row").get(containerIndex);
				for(Element tbody: resultTable.select("table tbody")){
					String statusLocation = tbody.select("tr:nth-of-type(1) td").text();
					for(int index=2; index<=tbody.select("tr").size(); index++){
						String[] dateFormats = {AppConstants.MAE_DATE_FORMAT1, AppConstants.MAE_DATE_FORMAT2};
						Timestamp statusDate = AppUtils.convertStringToDate(tbody.select("tr:nth-of-type("+index+") td:nth-of-type(1)").text(), 
									dateFormats);
						String status = tbody.select("tr:nth-of-type("+index+") td:nth-of-type(2)").text();
						String statusVessel = "";
						String statusVoyage = "";
						Matcher m = StatusPattern.valueOf("p1").getPattern().matcher(status);
						if(m.matches()){
							status = m.group(1);
							statusVessel = m.group(2);
							statusVoyage = m.group(3);
						}
						carrierService.saveContainerStatus(carrierCode, containerNo, bookingRef, blNumber, 
									status, statusDate, statusLocation, statusVessel, statusVoyage);
						
					}
				}
				LOGGER.info("Status saved for container: "+containerNo);
		}
	}
}
