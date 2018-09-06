package com.fms.carrier.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.enums.CarrierName;
import com.fms.carrier.service.MaeCrawlerService;

@Component
public class MaeCarrierJob {

	@Autowired
	private MaeCrawlerService maeCrawlerService;
	
	@Autowired
	private CarrierDAO carrierDAO;
	
	@Scheduled(cron = "${cron.expression}")
	public void schedule(){
		maeCrawlerService.crawl(CarrierName.MAE.toString(), carrierDAO.getInputDataset(CarrierName.MAE.toString()));
	}
}
