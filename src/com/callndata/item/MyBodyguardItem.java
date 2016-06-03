package com.callndata.item;

public class MyBodyguardItem {

	String TrackId, Name, Number;

	public MyBodyguardItem() {

	}

	public MyBodyguardItem(String TrackId, String Name, String Number) {
		this.TrackId = TrackId;
		this.Name = Name;
		this.Number = Number;
	}

	public String getTrackId() {
		return TrackId;
	}

	public void setTrackId(String trackId) {
		TrackId = trackId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
	}

}
