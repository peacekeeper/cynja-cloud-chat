package biz.neustar.clouds.chat.util;

import java.io.Writer;
import java.text.DateFormat;

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

	public static void write(Writer writer, JsonElement jsonElement) {

		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setIndent("  ");
		gson.toJson(jsonElement, jsonWriter);
	}

	public static JsonObject connectionToJson(Connection connection) {

		JsonObject child1JsonObject = new JsonObject();
		child1JsonObject.add("child", new JsonPrimitive(connection.getChild1().toString()));
		child1JsonObject.add("approved", new JsonPrimitive(connection.isApproved1()));
		child1JsonObject.add("blocked", new JsonPrimitive(connection.isBlocked1()));

		JsonObject child2JsonObject = new JsonObject();
		child2JsonObject.add("child", new JsonPrimitive(connection.getChild2().toString()));
		child2JsonObject.add("approved", new JsonPrimitive(connection.isApproved2()));
		child2JsonObject.add("blocked", new JsonPrimitive(connection.isBlocked2()));

		JsonArray childrenJsonArray = new JsonArray();
		childrenJsonArray.add(child1JsonObject);
		childrenJsonArray.add(child2JsonObject);

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("children", childrenJsonArray);

		return jsonObject;
	}

	public static JsonObject logToJson(Log log) {

		return (JsonObject) gson.toJsonTree(log);
	}
}
