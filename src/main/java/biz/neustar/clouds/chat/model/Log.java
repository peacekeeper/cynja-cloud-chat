package biz.neustar.clouds.chat.model;

import java.util.Date;

public class Log {

	private String line;
	private Date date;

	public Log(String line, Date date) {
		super();
		this.line = line;
		this.date = date;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
