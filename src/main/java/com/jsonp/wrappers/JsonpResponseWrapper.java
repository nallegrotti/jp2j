package com.jsonp.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modifica la rta para que cumpla con los estándares de JSONP
 * 
 * @author nallegrotti
 * 
 */
public class JsonpResponseWrapper extends HttpServletResponseWrapper {

	private static final int DEFAULT_JSONP_STATUS = 200;
	private HttpServletRequest originalRequest;
	private Integer status = null;
	private Map<String, String> headersMap;
	private ServletOutputStream out = null;
	private PrintWriter writer = null;
	private ByteArrayOutputStream bufferOut;
	private ServletOutputStream bufferedOutput;

	public JsonpResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	/**
	 * Devuelve los headers de la rta como JSON
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private String getHeadersString() throws JSONException {
		Map<String, String> headers = getHeaders();
		JSONObject json = new JSONObject();
		for (Map.Entry<String, String> h : headers.entrySet()) {
			json.put(h.getKey(), h.getValue());
		}
		return json.toString();
	}

	@Override
	public void setStatus(int sc) {
		status = sc;
		super.setStatus(DEFAULT_JSONP_STATUS);
	}

	@Override
	public void setStatus(int sc, String sm) {
		status = sc;
		super.setStatus(DEFAULT_JSONP_STATUS, sm);
	}

	@Override
	public void setHeader(String name, String value) {
		if (!name.equals("Content-Length")) {
			Map<String, String> headers = getHeaders();
			headers.put(name, value);
			super.setHeader(name, value);
		}
	}

	@Override
	public void setContentType(String type) {
		setHeader("Content-Type", type);
		super.setContentType(type);
	}
	
	private Map<String, String> getHeaders() {
		if (headersMap == null) {
			headersMap = new HashMap<String, String>();
		}
		return headersMap;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (bufferedOutput == null) {
			bufferOut = new ByteArrayOutputStream();
			bufferedOutput = new ServletOutputStreamWrapper(bufferOut);
		}
		return bufferedOutput;
	}
	
	
	
	@Override
	public void flushBuffer() throws IOException {
		printResponse();
		out.flush();
	}

	private void printResponse() throws IOException {
		String headers;
		try {
			headers = getHeadersString();
		} catch (JSONException e) {
			throw new IOException("Can't copy JSONP headers", e);
		}
		String originalEncoding = getCharacterEncoding();
		setContentType("text/javascript");
		setCharacterEncoding(originalEncoding);// NEW
		getHeaders().put("Content-Type", "text/javascript");

		out = super.getOutputStream();
		out.print(originalRequest.getParameter("callback"));
		out.print("([");
		
		if (status!=null) {
			out.print(status);
		}else {
			out.print("200");
		}
		out.print(", ");
		
		out.print(headers);
		
		out.print(", ");
		
		out.write(bufferOut.toByteArray());
		
		out.print("]);");
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer==null) {
			writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), Charset.forName(getCharacterEncoding())));
		}
		return writer;
	}

	public static HttpServletResponse getInstance(
			HttpServletResponse originalResponse,
			HttpServletRequest originalRequest) {
		if (JsonpRequestWrapper.isJsonp(originalRequest)) {
			JsonpResponseWrapper jsonpResponseWrapper = new JsonpResponseWrapper(
					originalResponse);
			jsonpResponseWrapper.setOriginalRequest(originalRequest);
			return jsonpResponseWrapper;
		} else {
			return originalResponse;
		}
	}

	public void setOriginalRequest(HttpServletRequest originalRequest) {
		this.originalRequest = originalRequest;
	}

	public HttpServletRequest getOriginalRequest() {
		return originalRequest;
	}
}
