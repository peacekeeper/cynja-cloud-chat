package biz.neustar.clouds.chat.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.websocket.Session;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.service.SessionService;

public class DefaultSessionService implements SessionService {

	private Map<Integer, LinkedList<Session>> sessionMap;

	public DefaultSessionService() {
		
		this.sessionMap = new HashMap<Integer, LinkedList<Session>> ();
	}
	
	public void addSession(Connection connection, Session session) {

		int hashCode = connection.getChild1().hashCode() * connection.getChild2().hashCode();
		LinkedList<Session> sessionList = this.sessionMap.get(Integer.valueOf(hashCode));

		if (sessionList == null) {

			sessionList = new LinkedList<Session> ();
			this.sessionMap.put(Integer.valueOf(hashCode), sessionList);
		}

		sessionList.add(session);
	}

	public void removeSession(Connection connection, Session session) {

		int hashCode = connection.getChild1().hashCode() * connection.getChild2().hashCode();
		LinkedList<Session> sessionList = this.sessionMap.get(Integer.valueOf(hashCode));

		if (sessionList == null) return;
		
		sessionList.remove(session);
	}

	public Session[] getSessions(Connection connection) {

		int hashCode = connection.getChild1().hashCode() * connection.getChild2().hashCode();
		LinkedList<Session> sessionList = this.sessionMap.get(Integer.valueOf(hashCode));

		if (sessionList == null) return new Session[0];

		return sessionList.toArray(new Session[sessionList.size()]);
	}
}
