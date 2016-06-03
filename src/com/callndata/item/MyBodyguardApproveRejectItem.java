package com.callndata.item;

public class MyBodyguardApproveRejectItem {

	String reqId, reqBy, name, number;

	public MyBodyguardApproveRejectItem() {

	}

	public MyBodyguardApproveRejectItem(String reqId, String reqBy, String name, String number) {
		this.reqId = reqId;
		this.reqBy = reqBy;
		this.name = name;
		this.number = number;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getReqBy() {
		return reqBy;
	}

	public void setReqBy(String reqBy) {
		this.reqBy = reqBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
