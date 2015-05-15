package biz.neustar.clouds.chat.service;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;

public interface LogService {

	public void addLog(Connection connection, String line);
	public Log[] getLogs(Connection connection);
}
