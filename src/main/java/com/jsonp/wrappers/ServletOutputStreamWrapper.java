package com.jsonp.wrappers;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.apache.commons.collections.Closure;

public class ServletOutputStreamWrapper extends ServletOutputStream {

	private ServletOutputStream out;
	private Closure beforeClose;

	public Closure getBeforeClose() {
		return beforeClose;
	}

	public void setBeforeClose(Closure beforeClose) {
		this.beforeClose = beforeClose;
	}

	public void close() throws IOException {
		beforeClose.execute(out);
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

	public void print(boolean b) throws IOException {
		out.print(b);
	}

	public void print(char c) throws IOException {
		System.out.println("*");
		out.print(c);
	}

	public void print(double d) throws IOException {
		out.print(d);
	}

	public void print(float f) throws IOException {
		out.print(f);
	}

	public void print(int i) throws IOException {
		out.print(i);
	}

	public void print(long l) throws IOException {
		out.print(l);
	}

	public void print(String s) throws IOException {
		out.print(s);
	}

	public void println() throws IOException {
		out.println();
	}

	public void println(boolean b) throws IOException {
		out.println(b);
	}

	public void println(char c) throws IOException {
		out.println(c);
	}

	public void println(double d) throws IOException {
		out.println(d);
	}

	public void println(float f) throws IOException {
		out.println(f);
	}

	public void println(int i) throws IOException {
		out.println(i);
	}

	public void println(long l) throws IOException {
		out.println(l);
	}

	public void println(String s) throws IOException {
		out.println(s);
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

	public ServletOutputStreamWrapper(ServletOutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

}
