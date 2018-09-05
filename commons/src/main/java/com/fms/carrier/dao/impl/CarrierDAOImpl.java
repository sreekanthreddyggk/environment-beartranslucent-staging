package com.fms.carrier.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fms.carrier.dao.CarrierDAO;
import com.fms.carrier.dao.mapper.DataMapper;
import com.fms.carrier.dto.ResultTableDTO;
import com.fms.carrier.dto.SourceTableDTO;

@Repository
public class CarrierDAOImpl implements CarrierDAO{

	@Autowired
	private DataMapper dataMapper;
	
	public List<SourceTableDTO> getInputDataset(String carrier) {
		return dataMapper.getInputDataset(carrier);
	}

	public int saveContainerStatus(ResultTableDTO resultTableDTO) {
		return dataMapper.saveContainerStatus(resultTableDTO);
	}

	public int updateInputProcessingTimeAndStatus(String carrier, String containerno, String bookingRef, 
			String blNumber, String status, Timestamp processingTimestamp) {
		return dataMapper.updateInputProcessingTimeAndStatus(carrier, containerno, bookingRef, blNumber, status, processingTimestamp);
	}

}
