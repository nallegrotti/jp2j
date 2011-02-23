package com.jsonp.wrappers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.collections.Closure;
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
    private int status;
    private Map<String, String> headersMap;
    private ServletOutputStream out=null;

    public JsonpResponseWrapper(HttpServletResponse response) {
	super(response);
    }

    /**
     * Copia los headers de la rta en el body como JSON 
     * 
     * @param out
     * @param resp
     * @throws IOException
     * @throws JSONException 
     */
    private void copyHeaders(ServletOutputStream out) throws IOException, JSONException {
	Map<String, String> headers = getHeaders();
	JSONObject json = new JSONObject();
	for (Map.Entry<String, String> h : headers.entrySet()){
	    json.put(h.getKey(), h.getValue());
	}
	out.print(json.toString());
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
        if("Content-Type".equals(name)){
            setContentType(value);
        }
    }

    private Map<String, String> getHeaders() {
	if (headersMap==null){
	    headersMap = new HashMap<String, String>();
	}
	return headersMap;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (out==null){
            out = super.getOutputStream();
            out.print(originalRequest.getParameter("callback"));
            out.print("([");
            out.print(status);
            out.print(", ");
            try {
		copyHeaders(out);
	    } catch (JSONException e) {
		throw new IOException("Can't copy JSONP headers", e);
	    }
            out.print(", ");

            String originalEncoding = getCharacterEncoding();
            setContentType("text/javascript");
            setCharacterEncoding(originalEncoding);//NEW
            getHeaders().put("Content-Type", "text/javascript");
            
            ServletOutputStreamWrapper servletOutputStreamWrapper = new ServletOutputStreamWrapper(out);
	    servletOutputStreamWrapper.setBeforeClose(new Closure() {
	        
	        @Override
	        public void execute(Object arg0) {
	    		ServletOutputStream out = (ServletOutputStream) arg0;
	    		try {
			    out.print("]);");
			} catch (IOException e) {
			    throw new RuntimeException("Can't close JSONP response properly", e);
			}
	        }
	    });
	    out = servletOutputStreamWrapper;
        }
        return out;
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(getOutputStream());
    }
    
    public static HttpServletResponse getInstance(
	    HttpServletResponse originalResponse,
	    HttpServletRequest originalRequest) {
	if (JsonpRequestWrapper.isJsonp(originalRequest)){
	    JsonpResponseWrapper jsonpResponseWrapper = new JsonpResponseWrapper(originalResponse);
	    jsonpResponseWrapper.setOriginalRequest(originalRequest);
	    return jsonpResponseWrapper;
	}else{
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
