package com.interpark.netty;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtil {
	private StringBuffer sB = new StringBuffer();
	private long startTime;
	private long finishTime;

	public void startJob() {
		startTime = System.nanoTime();
	}

	public void finishJob() {
		finishTime = System.nanoTime();
	}

	private void setSbLength() {
		sB.setLength(0);
	}

	public String executeTimeLog(long startTime, long finishTime){
		setSbLength();
		sB.append("execute ").append((finishTime - startTime) / 1000000).append(" ms");
		return sB.toString();
	}

	public String executeTimeLog(){
		setSbLength();
		sB.append("execute ").append((this.finishTime - this.startTime) / 1000000).append(" ms");
		return sB.toString();
	}

	public long executeTime() {
		return (this.finishTime - this.startTime) / 1000000;
	}

	public String catchLog(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.getBuffer().toString();
	}

}