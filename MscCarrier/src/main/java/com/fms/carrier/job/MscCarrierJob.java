package com.fms.carrier.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.enums.CarrierName;
import com.fms.carrier.service.MscCrawlerService;

@Component
public class MscCarrierJob {

	@Autowired
	private MscCrawlerService mscCrawlerService;
	
	@Autowired
	private CarrierDAO carrierDAO;
	
	@Scheduled(cron = "${cron.expression}")
	public void schedule(){
		mscCrawlerService.crawl(CarrierName.MSC.toString(), carrierDAO.getInputDataset(CarrierName.MSC.toString()));
	}
}
