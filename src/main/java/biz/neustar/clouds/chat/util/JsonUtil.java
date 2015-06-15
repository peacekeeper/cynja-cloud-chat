package biz.neustar.clouds.chat.util;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;

import javax.websocket.Session;

import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class JsonUtil {

	private static final Gson gson = new GsonBuilder()
	.setDateFormat(DateFormat.FULL, DateFormat.FULL)
	.disableHtmlEscaping()
	.serializeNulls()
	.create();

	public static String toString(JsonElement jsonElement) {

		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		gson.toJson(jsonElement, jsonWriter);
		return stringWriter.getBuffer().toString();
	}

	public static void write(Writer writer, JsonElement jsonElement) {

		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setIndent("  ");
		gson.toJson(jsonElement, jsonWriter);
	}

	public static JsonObject connectionsToJson(Connection[] connections) {

		JsonObject childrenJsonObject = new JsonObject();

		for (Connection connection : connections) {

			JsonArray childJsonArray = childrenJsonObject.getAsJsonArray(connection.getChild1().toString());

			if (childJsonArray == null) {

				childJsonArray = new JsonArray();
				childrenJsonObject.add(connection.getChild1().toString(), childJsonArray);
			}

			JsonObject child1JsonObject = new JsonObject();
			child1JsonObject.add("child", gson.toJsonTree(connection.getChild2().toString()));
			child1JsonObject.add("approved", gson.toJsonTree(connection.isApproved1()));
			child1JsonObject.add("blocked", gson.toJsonTree(connection.isBlocked1()));

			Session[] sessions = CynjaCloudChat.sessionService.getSessions(connection);

			JsonArray sessionsJsonArray = new JsonArray();

			for (Session session : sessions) {

				JsonObject sessionJsonObject = new JsonObject();
				sessionJsonObject.add("id", gson.toJsonTree(session.getId()));
				sessionJsonObject.add("open", gson.toJsonTree(session.isOpen()));

				sessionsJsonArray.add(sessionJsonObject);
			}

			child1JsonObject.add("sessions", sessionsJsonArray);

			childJsonArray.add(child1JsonObject);
		}

		return childrenJsonObject;
	}

	public static JsonObject logToJson(Log log) {

		JsonObject logJsonObject = new JsonObject();
		logJsonObject.add("chatChild1", new JsonPrimitive(log.getFromWebSocketMessageHandler().getChild1().toString()));
		logJsonObject.add("chatChild2", new JsonPrimitive(log.getFromWebSocketMessageHandler().getChild2().toString()));
		logJsonObject.add("connectionChild1", new JsonPrimitive(log.getFromWebSocketMessageHandler().getConnection().getChild1().toString()));
		logJsonObject.add("connectionChild2", new JsonPrimitive(log.getFromWebSocketMessageHandler().getConnection().getChild2().toString()));
		logJsonObject.add("message", new JsonPrimitive(log.getLine()));
		logJsonObject.add("date", gson.toJsonTree(log.getDate()));

		return logJsonObject;
	}
}
