package biz.neustar.clouds.chat.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.model.Connection;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketMessageHandler.class);

	private Session session;
	private XDIAddress child1;
	private XDIAddress child2;
	private Connection connection;

	public WebSocketMessageHandler(Session session, XDIAddress child1, XDIAddress child2, Connection connection) {

		this.session = session;
		this.child1 = child1;
		this.child2 = child2;
		this.connection = connection;
	}

	@Override
	public void onMessage(Reader reader) {

		// read line

		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;

		try {

			line = bufferedReader.readLine();
			line = this.getChild1().toString() + "> " + line;
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		log.info("Received line " + line + " from session " + this.session.getId());

		// log line

		CynjaCloudChat.logService.addLog(this.connection, line);

		// send line to message handlers

		WebSocketEndpoint.send(this, line);
	}

	public void send(String line) {

		this.session.getAsyncRemote().sendText(line);

		log.info("Sent line " + line + " to session " + this.session.getId());
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public XDIAddress getChild1() {

		return this.child1;
	}

	public XDIAddress getChild2() {

		return this.child2;
	}

	public Connection getConnection() {

		return this.connection;
	}
}
