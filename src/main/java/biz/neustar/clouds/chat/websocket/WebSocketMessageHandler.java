package biz.neustar.clouds.chat.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketMessageHandler.class);

	private Session session;
	private String path;
	private XDIAddress fromChild;
	private XDIAddress toChild;

	public WebSocketMessageHandler(Session session, String path, XDIAddress fromChild, XDIAddress toChild) {

		this.session = session;
		this.path = path;
		this.fromChild = fromChild;
		this.toChild = toChild;
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

		// send to sessions

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

	public String getPath() {

		return this.path;
	}

	public XDIAddress getFromChild() {

		return this.fromChild;
	}

	public XDIAddress getToChild() {

		return this.toChild;
	}
}
