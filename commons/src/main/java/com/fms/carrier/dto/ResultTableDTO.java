package com.fms.carrier.dto;

import java.sql.Timestamp;

public class ResultTableDTO {

	private int id;
	private String carrier;
	private String bookingRef;
	private String blNumber;
	private String containerno;
	private String status;
	private Timestamp statusDate;
	private String statusLocation;
	private String statusVessel;
	private String statusVoyage;
	
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
	public String getContainerno() {
		return containerno;
	}
	public void setContainerno(String containerno) {
		this.containerno = containerno;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(Timestamp statusDate) {
		this.statusDate = statusDate;
	}
	public String getStatusLocation() {
		return statusLocation;
	}
	public void setStatusLocation(String statusLocation) {
		this.statusLocation = statusLocation;
	}
	public String getStatusVessel() {
		return statusVessel;
	}
	public void setStatusVessel(String statusVessel) {
		this.statusVessel = statusVessel;
	}
	public String getStatusVoyage() {
		return statusVoyage;
	}
	public void setStatusVoyage(String statusVoyage) {
		this.statusVoyage = statusVoyage;
	}
	
	
}
