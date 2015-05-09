package biz.neustar.clouds.chat.websocket;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.model.Connection;

public class WebSocketEndpoint extends javax.websocket.Endpoint {

	private static final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);

	private static final String PATH = "/1/chat/{fromChild}/{toChild}";

	public static final List<WebSocketMessageHandler> WEBSOCKETMESSAGEHANDLERS = new ArrayList<WebSocketMessageHandler> ();

	public static void install(ServletContext servletContext) throws DeploymentException {

		// find server container

		ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
		if (serverContainer == null) throw new DeploymentException("Cannot find ServerContainer");

		// init websocket endpoint

		List<String> subprotocols = Arrays.asList(new String[] { "cynja-chat" });
		List<Extension> extensions = null;
		List<Class<? extends Encoder>> encoders = null;
		List<Class<? extends Decoder>> decoders = null;

		ServerEndpointConfig.Configurator serverEndpointConfigConfigurator = new ServerEndpointConfig.Configurator() {

		};

		ServerEndpointConfig.Builder serverEndpointConfigBuilder = ServerEndpointConfig.Builder.create(
				WebSocketEndpoint.class, 
				PATH);

		serverEndpointConfigBuilder.subprotocols(subprotocols);
		serverEndpointConfigBuilder.extensions(extensions);
		serverEndpointConfigBuilder.encoders(encoders);
		serverEndpointConfigBuilder.decoders(decoders);
		serverEndpointConfigBuilder.configurator(serverEndpointConfigConfigurator);

		ServerEndpointConfig serverEndpointConfig = serverEndpointConfigBuilder.build();

		serverContainer.addEndpoint(serverEndpointConfig);

		// done

		log.info("Installed WebSocket endpoint at " + PATH + " with subprotocols " + subprotocols);
	}

	public static void send(WebSocketMessageHandler fromWebSocketMessageHandler, String line) {

		for (WebSocketMessageHandler webSocketMessageHandler : WEBSOCKETMESSAGEHANDLERS) {

			if ((fromWebSocketMessageHandler.getFromChild().equals(webSocketMessageHandler.getToChild()) &&
					fromWebSocketMessageHandler.getToChild().equals(webSocketMessageHandler.getFromChild())) || (
							fromWebSocketMessageHandler.getFromChild().equals(webSocketMessageHandler.getFromChild()) &&
							fromWebSocketMessageHandler.getToChild().equals(webSocketMessageHandler.getToChild()))) {

				webSocketMessageHandler.send(line);
			}
		}
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// set timeout

		long oldMaxIdleTimeout = session.getMaxIdleTimeout();
		long newMaxIdleTimeout = 0;
		session.setMaxIdleTimeout(newMaxIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed max idle timeout of session " + session.getId() + " from " + oldMaxIdleTimeout + " to " + newMaxIdleTimeout);

		// init message handler

		ServerEndpointConfig serverEndpointConfig = (ServerEndpointConfig) endpointConfig;

		try {

			// parse parameters

			XDIAddress fromChild = XDIAddress.create(URLDecoder.decode(session.getPathParameters().get("fromChild"), "UTF-8"));
			XDIAddress toChild = XDIAddress.create(URLDecoder.decode(session.getPathParameters().get("toChild"), "UTF-8"));

			// check connection

			Connection connection = CynjaCloudChat.connectionService.findConnection(fromChild, toChild);

			if (connection == null) {

				session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Connection not found."));
				return;
			}

			if (! connection.isApproved1() || ! connection.isApproved2()) {

				session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Connection is not approved yet."));
				return;
			}

			if (connection.isBlocked1() || connection.isBlocked2()) {

				session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Connection is temporarily blocked."));
				return;
			}

			// add session to connection

			connection.addSession(session);

			// create message handler

			WebSocketMessageHandler webSocketMessageHandler = new WebSocketMessageHandler(session, fromChild, toChild, connection);

			session.addMessageHandler(webSocketMessageHandler);
			WEBSOCKETMESSAGEHANDLERS.add(webSocketMessageHandler);

			log.info("WebSocket session " + session.getId() + " opened (" + serverEndpointConfig.getPath() + ") between " + fromChild + " and " + toChild);
		} catch (Exception ex) {

			try {

				session.close(new CloseReason(CloseCodes.PROTOCOL_ERROR, "Cannot add message handler: " + ex.getMessage()));
			} catch (IOException ex2) {

				throw new RuntimeException(ex2.getMessage(), ex2);
			}
		}
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		// find message handler and connection

		WebSocketMessageHandler webSocketMessageHandler = (WebSocketMessageHandler) session.getMessageHandlers().iterator().next();
		Connection connection = webSocketMessageHandler.getConnection();

		// remove session from connection

		connection.removeSession(session);

		// remove message handler

		session.removeMessageHandler(webSocketMessageHandler);
		WEBSOCKETMESSAGEHANDLERS.remove(webSocketMessageHandler);

		log.info("WebSocket session " + session.getId() + " closed.");
	}

	@Override
	public void onError(Session session, Throwable throwable) {

		log.error("WebSocket session " + session.getId() + " error: " + throwable.getMessage(), throwable);
	}
}