package com.fms.carrier.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fms.carrier.constants.AppConstants;
import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.dto.SourceTableDTO;
import com.fms.carrier.util.AppUtils;

@Service
public class MscCrawlerService {

	@Autowired
	private CarrierDAO carrierDAO;
	
	@Autowired
	private CarrierService carrierService;
	
	@Value("${carrier.url}")
	private String carrierUrl;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MscCrawlerService.class);
	
	/*
	 * Functionality to load,crawl the webpage and save status of container in output table
	 */
	public void crawl(String carrierCode, List<SourceTableDTO> inputList){
		try{
			LOGGER.info("Started crawling for carrier: "+carrierCode);
			WebDriver driver = AppUtils.getDriverInstance(carrierUrl);
			driver.navigate().refresh();
			LOGGER.info("Web driver instantiated for carrier: "+carrierCode);
			LOGGER.info("Loaded webpage for carrier: "+carrierCode);
			
			if(driver.findElements(By.id("ctl00_ctl00_NewsetterSignupPopup_btnReject")).size()>0 
					&& driver.findElement(By.id("ctl00_ctl00_NewsetterSignupPopup_btnReject")).isDisplayed()){
				driver.findElement(By.id("ctl00_ctl00_NewsetterSignupPopup_btnReject")).click();
			}
			
			WebDriverWait explicitWait = new WebDriverWait(driver, 30000);
			if(driver.findElements(By.className("reveal-modal-bg")).size()>0){
				explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("reveal-modal-bg")));
			}
			
			driver.findElement(By.id("btnTrack")).click();
			if(driver.findElements(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_TextField")).size()>0){
				explicitWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_TextField")));
			}
			
			Document page = Jsoup.parse(driver.getPageSource());
			
			String errorText = "No matching tracking information. Please try again.";
			for(SourceTableDTO input: inputList){
				String status = AppConstants.SUCCESS;
				try{
					Document inputPage = getPageDocument(driver, input.getBookingRef(), page);
					if(inputPage.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults").size() == 0 || 
							inputPage.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults h3").text().equals(errorText)){
						LOGGER.info("Entered booking reference-"+input.getBookingRef()+" is not valid");
						inputPage = getPageDocument(driver, input.getBlNumber(), page);
						
						if(inputPage.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults").size() == 0 || 
								inputPage.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults h3").text().equals(errorText)){
							LOGGER.info("Entered bl number-"+input.getBlNumber()+" is not valid");
							inputPage = getPageDocument(driver, input.getContainerno(), page);
							
							if(inputPage.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults").size() == 0 || 
									inputPage.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults h3").text().equals(errorText)){
								status = AppConstants.NOT_FOUND;
								LOGGER.info("Entered container number-"+input.getContainerno()+" is not valid");
							}else{
								LOGGER.info("Crawling started for container :"+input.getContainerno());
								crawlContainer(driver, inputPage, carrierCode, "", "");
								LOGGER.info("Crawling completed for container :"+input.getContainerno());
							}
						}else{
							LOGGER.info("Crawling started for bl number :"+input.getBlNumber());
							crawlContainer(driver, inputPage, carrierCode, "", input.getBlNumber());
							LOGGER.info("Crawling completed for bl number :"+input.getBlNumber());
						}
					}else{
						LOGGER.info("Crawling started for booking reference :"+input.getBookingRef());
						crawlContainer(driver, inputPage, carrierCode, input.getBookingRef(), input.getBlNumber());
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
	
	private Document getPageDocument(WebDriver driver, String input, Document page){
		if(driver.findElement(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_TextField")).isDisplayed()){
			driver.findElement(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_TextField")).clear();
			driver.findElement(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_TextField")).sendKeys(input);
			driver.findElement(By.id("ctl00_ctl00_Header_TrackSearch_hlkSearch")).click();
			if((driver.findElements(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_RequiredValidator")).size() != 0 
					&& driver.findElement(By.id("ctl00_ctl00_Header_TrackSearch_txtBolSearch_RequiredValidator")).isDisplayed())){
				return page;
			}
		}else{
			driver.findElement(By.id("ctl00_ctl00_plcMain_plcMain_TrackSearch_txtBolSearch_TextField")).clear();
			driver.findElement(By.id("ctl00_ctl00_plcMain_plcMain_TrackSearch_txtBolSearch_TextField")).sendKeys(input);
			driver.findElement(By.id("ctl00_ctl00_plcMain_plcMain_TrackSearch_hlkSearch")).click();
			if((driver.findElements(By.id("ctl00_ctl00_plcMain_plcMain_TrackSearch_txtBolSearch_RequiredValidator")).size() != 0 
					&& driver.findElement(By.id("ctl00_ctl00_plcMain_plcMain_TrackSearch_txtBolSearch_RequiredValidator")).isDisplayed())){
				return page;
			}
		}
		return Jsoup.parse(driver.getPageSource());
	}
	
	private void crawlContainer(WebDriver driver, Document page, String carrierCode, String bookingRef, String blNumber) throws Exception{
		int noOfBol = page.select("#ctl00_ctl00_plcMain_plcMain_pnlTrackingResults dl dd[id$=\"_BOLItem\"]").size();
		for(int bolIndex=0; bolIndex<noOfBol; bolIndex++){
			
			int noOfContainers = page.select("#ctl00_ctl00_plcMain_plcMain_rptBOL_ctl"+String.format("%02d",bolIndex)+
					"_pnlBOLContent dd").size();
			for(int containerIndex=1; containerIndex<=noOfContainers; containerIndex++){
				Elements data = page.select("#ctl00_ctl00_plcMain_plcMain_rptBOL_ctl"+String.format("%02d",bolIndex)+
						"_rptContainers_ctl"+String.format("%02d",containerIndex)+"_hlkContainerToggle");
				String containerNo = data.text().substring(data.text().indexOf(" ")+1);
				
				Elements resultTable = page.select("#ctl00_ctl00_plcMain_plcMain_rptBOL_ctl"+String.format("%02d",bolIndex)+
						"_rptContainers_ctl"+String.format("%02d",containerIndex)+"_pnlContainer table.resultTable tbody tr");
				for(Element row: resultTable){
					String[] dateFormats = {AppConstants.MSC_DATE_FORMAT};
					Timestamp statusDate = AppUtils.convertStringToDate(row.select("td:nth-of-type(3) span").text(), dateFormats);
					carrierService.saveContainerStatus(carrierCode, containerNo, bookingRef, blNumber, 
							row.select("td:nth-of-type(2) span").text(), statusDate, 
							row.select("td:nth-of-type(1) span").text(), row.select("td:nth-of-type(4) span").text(), 
							row.select("td:nth-of-type(5) span").text());
				}
				LOGGER.info("Status saved for container: "+containerNo);
			}
		}
	}
	
}
