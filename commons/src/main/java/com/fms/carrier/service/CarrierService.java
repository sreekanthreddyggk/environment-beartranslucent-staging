package com.fms.carrier.service;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.dto.ResultTableDTO;

@Service
public class CarrierService {
	
	@Autowired
	private CarrierDAO carrierDAO;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CarrierService.class);

	public void saveContainerStatus(String carrier, String containerno, String bookingRef, String blNumber, String status, Timestamp statusDate, 
			String statusLocation,  String statusVessel, String statusVoyage) {
		ResultTableDTO resultTableDTO = new ResultTableDTO();
		resultTableDTO.setContainerno(containerno);
		resultTableDTO.setCarrier(carrier);
		resultTableDTO.setBookingRef(bookingRef);
		resultTableDTO.setBlNumber(blNumber);
		resultTableDTO.setStatus(status);
		resultTableDTO.setStatusLocation(statusLocation);
		resultTableDTO.setStatusVessel(statusVessel);
		resultTableDTO.setStatusVoyage(statusVoyage);
		resultTableDTO.setStatusDate(statusDate);
		try {
			carrierDAO.saveContainerStatus(resultTableDTO);
		} catch (Exception e) {
			LOGGER.error("Exception while saving status for container: "+containerno);
			throw e;
		}
		
	}
}
