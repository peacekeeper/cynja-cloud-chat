package biz.neustar.clouds.chat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.LogService;

public class DefaultLogService implements LogService {

	public static final int MAX_LOG_SIZE = 20;

	private Map<Integer, LinkedList<Log>> logMap;

	public DefaultLogService() {

		this.logMap = new HashMap<Integer, LinkedList<Log>> ();
	}

	public void addLog(Connection connection, String line) {

		int hashCode = connection.getChild1().hashCode() * connection.getChild2().hashCode();
		LinkedList<Log> logList = this.logMap.get(Integer.valueOf(hashCode));

		if (logList == null) {

			logList = new LinkedList<Log> ();
			this.logMap.put(Integer.valueOf(hashCode), logList);
		}

		logList.add(new Log(connection, line, new Date()));
		if (logList.size() > MAX_LOG_SIZE) logList.pop();
	}

	public Log[] getLogs(Connection connection) {

		int hashCode = connection.getChild1().hashCode() * connection.getChild2().hashCode();
		List<Log> logList = this.logMap.get(Integer.valueOf(hashCode));

		if (logList == null) return new Log[0];

		return logList.toArray(new Log[logList.size()]);
	}
}
