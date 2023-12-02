package com.imservices.im.bmm.service;

import com.imservices.im.bmm.entity.WebConfig;

public interface WebConfigService {

	public WebConfig get() throws Exception;
	public WebConfig getOne() throws Exception;
	public void update(WebConfig wc) throws Exception;
	
}
