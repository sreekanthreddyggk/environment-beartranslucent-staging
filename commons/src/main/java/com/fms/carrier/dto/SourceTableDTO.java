package com.fms.carrier.dto;

import java.sql.Timestamp;

public class SourceTableDTO {
	
	private int id;
	private String carrier;
	private String bookingRef;
	private String blNumber;
	private String containerno;
	private Timestamp processingTimestamp;
	private String status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getBookingRef() {
		return bookingRef;
	}
	public void setBookingRef(String bookingRef) {
		this.bookingRef = bookingRef;
	}
	public String getBlNumber() {
		return blNumber;
	}
	public void setBlNumber(String blNumber) {
		this.blNumber = blNumber;
	}
	public Timestamp getProcessingTimestamp() {
		return processingTimestamp;
	}
	public void setProcessingTimestamp(Timestamp processingTimestamp) {
		this.processingTimestamp = processingTimestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getContainerno() {
		return containerno;
	}
	public void setContainerno(String containerno) {
		this.containerno = containerno;
	}
	
	
}
