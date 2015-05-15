package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.util.JsonUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ViewAsChildServlet extends HttpServlet {

	private static final long serialVersionUID = -5562095868444661045L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("child"));
		String parentSecretToken = req.getParameter("childSecretToken");

		Connection[] connections = CynjaCloudChat.connectionService.viewConnectionsAsChild(parent, parentSecretToken);

		JsonArray jsonArray = new JsonArray();

		for (Connection connection : connections) {

			JsonObject connectionJsonObject = JsonUtil.connectionToJson(connection);
			jsonArray.add(connectionJsonObject);
		}

		resp.setContentType("appliction/json");
		JsonUtil.write(resp.getWriter(), jsonArray);
	}
}