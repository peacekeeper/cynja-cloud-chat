package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.util.JsonUtil;

import com.google.gson.JsonObject;

public class ViewAsParentServlet extends HttpServlet {

	private static final long serialVersionUID = 2049298539409005496L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		String parentSecretToken = req.getParameter("parentSecretToken");

		Connection[] connections = CynjaCloudChat.connectionService.viewConnectionsAsParent(parent, parentSecretToken);

		JsonObject jsonObject = JsonUtil.connectionsToJson(connections);

		resp.setContentType("appliction/json");
		JsonUtil.write(resp.getWriter(), jsonObject);
	}
}
