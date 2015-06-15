package biz.neustar.clouds.chat.model;

import java.util.Date;

import biz.neustar.clouds.chat.websocket.WebSocketMessageHandler;

public class Log {

	private WebSocketMessageHandler fromWebSocketMessageHandler;
	private Connection connection;
	private String line;
	private Date date;

	public Log(WebSocketMessageHandler fromWebSocketMessageHandler, Connection connection, String line, Date date) {
		super();
		this.fromWebSocketMessageHandler = fromWebSocketMessageHandler;
		this.connection = connection;
		this.line = line;
		this.date = date;
	}
	public WebSocketMessageHandler getFromWebSocketMessageHandler() {
		return fromWebSocketMessageHandler;
	}
	public void setFromWebSocketMessageHandler(WebSocketMessageHandler fromWebSocketMessageHandler) {
		this.fromWebSocketMessageHandler = fromWebSocketMessageHandler;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
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
