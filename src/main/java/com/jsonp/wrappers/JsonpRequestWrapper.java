package com.jsonp.wrappers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;

/**
 * Limpia de parámetros propios de JSONP ap request
 * 
 * @author nallegrotti
 * 
 */
public class JsonpRequestWrapper extends HttpServletRequestWrapper {

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getHeaderNames() {
		@SuppressWarnings("unchecked")
		ArrayList<String> originales = Collections.list(super.getHeaderNames());
		originales.add("Content-Type");
		return Collections.enumeration(originales);
	}

	@SuppressWarnings("rawtypes")
	private Map parameterMap = null;

	private JsonpRequestWrapper(HttpServletRequest request) {
		super(request);
		Logger logger = Logger.getLogger(getClass());
		if (logger.isInfoEnabled()) {
			String body = getBody();
			String method = getMethod();
			String callback = super.getParameter("callback");
			logger.info("JSONP : callback=" + callback + ",method=" + method
					+ ", body={" + body + "}");
		}
	}

	public static HttpServletRequest getInstance(HttpServletRequest request) {
		Logger hlog = Logger.getLogger("headerLogger");
		if (hlog.isDebugEnabled()) {
			Enumeration<?> headerNames = request.getHeaderNames();
			StringBuilder headers = new StringBuilder();
			while (headerNames.hasMoreElements()) {
				String name = (String) headerNames.nextElement();
				String value = request.getHeader(name);
				headers.append(name).append(":").append(value);
			}
			hlog.debug("HEADERS: " + headers);
		}

		if (isJsonp(request)) {
			return new JsonpRequestWrapper(request);
		} else {
			return request;
		}
	}

	@Override
	public String[] getParameterValues(String name) {
		@SuppressWarnings("rawtypes")
		Map map = getParameterMap();
		return (String[]) map.get(name);
	}

	@Override
	public String getMethod() {
		String method = super.getParameter("_method");
		return method != null ? method : super.getMethod();
	}

	@Override
	public String getParameter(String name) {
		Object param = getParameterMap().get(name);
		if (param != null) {
			if (param.getClass().isAssignableFrom(String.class)) {
				return (String) param;
			} else {
				return ((String[]) param)[0];
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getParameterMap() {
		if (parameterMap == null) {
			parameterMap = new HashMap(super.getParameterMap());
			parameterMap.remove("callback");
			parameterMap.remove("_method");
			parameterMap.remove("_body");
		}
		return parameterMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Enumeration getParameterNames() {
		Set ks = getParameterMap().keySet();
		return Collections.enumeration(ks);
	}

	public static boolean isJsonp(HttpServletRequest request) {
		return request.getMethod().equals("GET")
				&& request.getParameter("callback") != null;
	}

	@Override
	public String getHeader(String name) {
		if (name.equalsIgnoreCase("Accept")) {
			return "application/json";
		} else if (name.equalsIgnoreCase("Content-Type")) {
			return "application/json";
		} else {
			return super.getHeader(name);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Enumeration getHeaders(String name) {
		if (name.equalsIgnoreCase("Accept")) {
			return new StringTokenizer("application/json");
		} else if (name.equalsIgnoreCase("Content-Type")) {
			return new StringTokenizer("application/json");
		} else {
			return super.getHeaders(name);
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final String bodyString = getBody();
		setCharacterEncoding("UTF-8");

		return new ServletInputStream() {
			private InputStream body = new ByteArrayInputStream(
					bodyString.getBytes("UTF-8"));

			public int read() throws IOException {
				return body.read();
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		String body = getBody();
		return new BufferedReader(new StringReader(body));
	}

	public String getBody() {
		String body = super.getParameter("_body");
		return body != null ? super.getParameter("_body") : "";
	}

	@Override
	public String getQueryString() {
		String originalQueryString = super.getQueryString();
		if (originalQueryString!=null) {
			StringBuilder queryString = new StringBuilder();
			String separador = "";
			for (String param : originalQueryString.split("&")) {
				if (!param.startsWith("callback=")
						&& !param.startsWith("_method=")
						&& !param.startsWith("_body=")) {
					queryString.append(separador);
					queryString.append(param);
					separador = "&";
				}
			}
			return queryString.toString();
		}else {
			return null;
		}
	}
}
