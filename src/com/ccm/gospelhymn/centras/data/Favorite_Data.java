/*
 * com.ziofront.android.contacts
 * Contact.java
 * Jiho Park    2009. 11. 27.
 *
 * Copyright (c) 2009 ziofront.com. All Rights Reserved.
 */
package com.ccm.gospelhymn.centras.data;


public class Favorite_Data {
	int _id;
	String id; 
	String num;
	String subject;
	String thumb;
	String portal;
	public Favorite_Data(int _id, String id, String num, String subject, String thumb, String portal){
		this._id = _id;
		this.id = id;
		this.num = num;
		this.subject = subject;
		this.thumb = thumb;
		this.portal = portal;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getPortal() {
		return portal;
	}
	public void setPortal(String portal) {
		this.portal = portal;
	}
	
}