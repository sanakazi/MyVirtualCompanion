package com.callndata.item;

public class TrackDetailsItem {

	String latitude, longitude, currentLocation, insertDate;

	public TrackDetailsItem() {

	}

	public TrackDetailsItem(String latitude, String longitude, String currentLocation, String insertDate) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.currentLocation = currentLocation;
		this.insertDate = insertDate;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

}
