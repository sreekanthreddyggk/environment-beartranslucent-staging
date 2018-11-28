package com.fms.carrier.dao.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.fms.carrier.dto.ResultTableDTO;
import com.fms.carrier.dto.SourceTableDTO;

@Mapper
public interface DataMapper {

	String getInputDatasetIncludingErrors = "select * from webtracking_input where carrier = #{carrier} and (status is null or status not in('success','Not Found'))";

    String getInputDatasetUnprocessed = "select * from webtracking_input where carrier = #{carrier} and status is null";

	String saveContainerStatus = "insert into webtracking_output(carrier, booking_ref, bl_number, containerno, status, status_date, "
			+ "status_location, status_vessel, status_voyage) values(#{carrier}, #{bookingRef}, #{blNumber}, #{containerno}, "
			+ "#{status}, #{statusDate}, #{statusLocation}, #{statusVessel}, #{statusVoyage})";

	String updateInputProcessingTimeAndStatus = "update webtracking_input set processing_timestamp = #{processingTimestamp}, status = #{status} "
			+ "where carrier=#{carrier} and booking_ref = #{bookingRef} and bl_number=#{blNumber} and containerno = #{containerno}";

	@Select(getInputDatasetUnprocessed)
	@Results({ @Result(property = "bookingRef", column = "booking_ref"),
			@Result(property = "blNumber", column = "bl_number"),
			@Result(property = "containerno", column = "containerno"),
			@Result(property = "carrier", column = "carrier"), })
	public List<SourceTableDTO> getInputDataset(@Param("carrier") String carrier);

	@Insert(saveContainerStatus)
	public int saveContainerStatus(ResultTableDTO resultTableDTO);

	@Update(updateInputProcessingTimeAndStatus)
	public int updateInputProcessingTimeAndStatus(
			@Param("carrier") String carrier,
			@Param("containerno") String containerno,
			@Param("bookingRef") String bookingRef,
			@Param("blNumber") String blNumber, @Param("status") String status,
			@Param("processingTimestamp") Timestamp processingTimestamp);
}
