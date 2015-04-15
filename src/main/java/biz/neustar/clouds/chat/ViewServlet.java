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

public class ViewServlet extends HttpServlet {

	private static final long serialVersionUID = 2049298539409005496L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parentOrChild = XDIAddress.create(req.getParameter("parentOrChild"));

		Connection[] connections = CynjaCloudChat.connectionService.viewConnections(parentOrChild);

		JsonArray jsonArray = new JsonArray();
		
		for (Connection connection : connections) {
			
			JsonObject connectionJsonObject = JsonUtil.connectionToJson(connection);
			jsonArray.add(connectionJsonObject);
		}
		
		resp.setContentType("appliction/json");
		JsonUtil.write(resp.getWriter(), jsonArray);
	}
}
