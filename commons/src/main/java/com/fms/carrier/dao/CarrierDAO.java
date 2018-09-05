package com.fms.carrier.dao;

import java.sql.Timestamp;
import java.util.List;

import com.fms.carrier.dto.ResultTableDTO;
import com.fms.carrier.dto.SourceTableDTO;

public interface CarrierDAO {

	public List<SourceTableDTO> getInputDataset(String carrier);
	
	public int saveContainerStatus(ResultTableDTO resultTableDTO);
	
	public int updateInputProcessingTimeAndStatus(String carrier, String containerno, String bookingRef, 
			String blNumber, String status, Timestamp processingTimestamp);
}
