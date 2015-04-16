package biz.neustar.clouds.chat.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketMessageHandler.class);

	private Session session;
	private XDIAddress fromChild;
	private XDIAddress toChild;
	private Connection connection;

	public WebSocketMessageHandler(Session session, XDIAddress fromChild, XDIAddress toChild, Connection connection) {

		this.session = session;
		this.fromChild = fromChild;
		this.toChild = toChild;
		this.connection = connection;
	}

	@Override
	public void onMessage(Reader reader) {

		// read line

		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;

		try {

			line = bufferedReader.readLine();
			line = this.getFromChild().toString() + "> " + line;
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		log.info("Received line " + line + " from session " + this.session.getId());

		// log line

		this.getConnection().addLog(line);

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

	public XDIAddress getFromChild() {

		return this.fromChild;
	}

	public XDIAddress getToChild() {

		return this.toChild;
	}

	public Connection getConnection() {

		return this.connection;
	}
}
