package biz.neustar.clouds.chat.util;

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
import com.google.gson.stream.JsonWriter;

public class JsonUtil {

	private static final Gson gson = new GsonBuilder()
	.setDateFormat(DateFormat.FULL, DateFormat.FULL)
	.disableHtmlEscaping()
	.serializeNulls()
	.create();

	public static void write(Writer writer, JsonElement jsonElement) {

		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setIndent("  ");
		gson.toJson(jsonElement, jsonWriter);
	}

	public static JsonObject connectionToJson(Connection connection) {

		JsonArray childrenJsonArray = new JsonArray();

		JsonObject child1JsonObject = new JsonObject();
		child1JsonObject.add("child", gson.toJsonTree(connection.getChild1().toString()));
		child1JsonObject.add("approved", gson.toJsonTree(connection.isApproved1()));
		child1JsonObject.add("blocked", gson.toJsonTree(connection.isBlocked1()));

		JsonObject child2JsonObject = new JsonObject();
		child2JsonObject.add("child", gson.toJsonTree(connection.getChild2().toString()));
		child2JsonObject.add("approved", gson.toJsonTree(connection.isApproved2()));
		child2JsonObject.add("blocked", gson.toJsonTree(connection.isBlocked2()));

		childrenJsonArray.add(child1JsonObject);
		childrenJsonArray.add(child2JsonObject);

		Session[] sessions = CynjaCloudChat.sessionService.getSessions(connection);
		
		JsonArray sessionsJsonArray = new JsonArray();

		for (Session session : sessions) {

			JsonObject sessionJsonObject = new JsonObject();
			sessionJsonObject.add("id", gson.toJsonTree(session.getId()));
			sessionJsonObject.add("open", gson.toJsonTree(session.isOpen()));

			sessionsJsonArray.add(sessionJsonObject);
		}

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("children", childrenJsonArray);
		jsonObject.add("sessions", sessionsJsonArray);

		return jsonObject;
	}

	public static JsonObject logToJson(Log log) {

		return (JsonObject) gson.toJsonTree(log);
	}
}
