/*
 * com.ziofront.android.contacts
 * Contact.java
 * Jiho Park    2009. 11. 27.
 *
 * Copyright (c) 2009 ziofront.com. All Rights Reserved.
 */
package com.ccm.gospelhymn.centras.data;

import java.util.Comparator;


public class Sub_Data implements Comparator<Sub_Data> {
	public String id;
	public String videoid;
	public String subject;
	public String portal;
	public String thumb;
	@Override
	public int compare(Sub_Data arg0, Sub_Data arg1) {
		// TODO Auto-generated method stub
		return arg0.subject.compareTo(arg1.subject);
	}
 }
