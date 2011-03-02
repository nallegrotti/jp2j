package com.jsonp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsonp.wrappers.JsonpRequestWrapper;
import com.jsonp.wrappers.JsonpResponseWrapper;

public class JsonpFilter implements Filter {
	
	private String headerSwitchName=null;
	private String headerSwitchValue=null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		try {
			HttpServletRequest originalRequest = (HttpServletRequest) request;
			HttpServletResponse originalResponse = (HttpServletResponse) response;
			if (isFilterActive(originalRequest, originalResponse)) {
				HttpServletRequest req = JsonpRequestWrapper
						.getInstance(originalRequest);
				HttpServletResponse res = JsonpResponseWrapper.getInstance(
						originalResponse, originalRequest);
				filterChain.doFilter(req, res);
				res.flushBuffer();
			} else {
				filterChain.doFilter(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			filterChain.doFilter(request, response);
		}

	}

	public boolean isFilterActive(HttpServletRequest originalRequest,
			HttpServletResponse originalResponse) {
		String switchName = getHeaderSwitchName();
		if (switchName!=null) {
			String filterSwitch = originalRequest.getHeader(switchName);
			String switchValue = getHeaderSwitchValue();
			return (switchValue==null && filterSwitch!=null) 
				|| (switchValue!=null && switchValue.equals(filterSwitch));
		}else {
			return true;
		}
	}

	public void setHeaderSwitch(String headerSwitch) {
		if (headerSwitch != null) {
			String[] headerSwitchPair = headerSwitch.split(":");
			headerSwitchName = headerSwitchPair.length >= 1 ? headerSwitchPair[0]
					: null;
			headerSwitchValue = headerSwitchPair.length >= 2 ? headerSwitchPair[1]
					: null;
		}		
	}

	@Override
	public void init(FilterConfig conf) throws ServletException {
		setHeaderSwitch(conf.getInitParameter("headerSwitch"));
	}

	public void setHeaderSwitchName(String headerSwitchName) {
		this.headerSwitchName = headerSwitchName;
	}

	public String getHeaderSwitchName() {
		return headerSwitchName;
	}

	public void setHeaderSwitchValue(String headerSwitchValue) {
		this.headerSwitchValue = headerSwitchValue;
	}

	public String getHeaderSwitchValue() {
		return headerSwitchValue;
	}
}
