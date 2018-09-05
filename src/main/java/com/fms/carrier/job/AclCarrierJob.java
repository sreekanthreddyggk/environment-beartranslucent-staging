package com.fms.carrier.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.enums.CarrierName;
import com.fms.carrier.service.AclCrawlerService;

@Component
public class AclCarrierJob {
	
	@Autowired
	private AclCrawlerService crawlerService;
	
	@Autowired
	private CarrierDAO carrierDAO;
	
	@Scheduled(cron = "${cron.expression}")
	public void schedule(){
		crawlerService.crawl(CarrierName.ACL.toString(), carrierDAO.getInputDataset(CarrierName.ACL.toString()));
	}
}
