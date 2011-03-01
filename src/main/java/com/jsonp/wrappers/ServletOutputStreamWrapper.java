package com.jsonp.wrappers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.commons.collections.Closure;

public class ServletOutputStreamWrapper extends ServletOutputStream {

	private OutputStream out;
	private Closure beforeClose;

	public Closure getBeforeClose() {
		return beforeClose;
	}

	public void setBeforeClose(Closure beforeClose) {
		this.beforeClose = beforeClose;
	}

	public void close() throws IOException {
		if (beforeClose!=null) {
			beforeClose.execute(out);
		}
		out.close();
	}

	public boolean equals(Object obj) {
		return out.equals(obj);
	}

	public void flush() throws IOException {
		out.flush();
	}

	public int hashCode() {
		return out.hashCode();
	}

	public String toString() {
		return out.toString();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public ServletOutputStreamWrapper(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

}
