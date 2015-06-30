package biz.neustar.clouds.chat.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.util.JsonUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<String> {

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
	public void onMessage(String string) {

		// read line

		BufferedReader bufferedReader = new BufferedReader(new StringReader(string));
		String line;

		try {

			line = bufferedReader.readLine();
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		log.info("Received line " + line + " from session " + this.session.getId());

		// log line

		CynjaCloudChat.logService.addLog(this, this.connection, line);

		// send line to message handlers

		WebSocketEndpoint.send(this, line);
	}

	public void send(WebSocketMessageHandler fromWebSocketMessageHandler, String line) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("chatChild1", new JsonPrimitive(fromWebSocketMessageHandler.getChild1().toString()));
		jsonObject.add("chatChild2", new JsonPrimitive(fromWebSocketMessageHandler.getChild2().toString()));
		jsonObject.add("connectionChild1", new JsonPrimitive(fromWebSocketMessageHandler.getConnection().getChild1().toString()));
		jsonObject.add("connectionChild2", new JsonPrimitive(fromWebSocketMessageHandler.getConnection().getChild2().toString()));
		jsonObject.add("message", new JsonPrimitive(line));

		String string = JsonUtil.toString(jsonObject);

		this.session.getAsyncRemote().sendText(string);

		log.info("Sent JSON object " + string + " to session " + this.session.getId());
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
