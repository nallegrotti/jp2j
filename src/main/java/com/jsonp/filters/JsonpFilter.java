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
		String filterSwitch = originalRequest.getHeader("X-Public");
		return "True".equals(filterSwitch);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
