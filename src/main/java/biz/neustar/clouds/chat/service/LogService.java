package biz.neustar.clouds.chat.service;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.websocket.WebSocketMessageHandler;

public interface LogService {

	public void addLog(WebSocketMessageHandler fromWebSocketMessageHandler, Connection connection, String line);
	public Log[] getLogs(Connection connection);
}
