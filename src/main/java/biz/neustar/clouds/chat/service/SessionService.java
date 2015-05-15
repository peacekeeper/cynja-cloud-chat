package biz.neustar.clouds.chat.service;

import javax.websocket.Session;

import biz.neustar.clouds.chat.model.Connection;

public interface SessionService {

	public void addSession(Connection connection, Session session);
	public void removeSession(Connection connection, Session session);
	public Session[] getSessions(Connection connection);
}
