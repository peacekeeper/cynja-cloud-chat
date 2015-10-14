package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.util.HexUtil;
import biz.neustar.clouds.chat.util.JsonUtil;

import com.google.gson.JsonObject;

public class ViewAsChildServlet extends HttpServlet {

	private static final long serialVersionUID = -5562095868444661045L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		XDIAddress child = XDIAddress.create(req.getParameter("child"));
		CloudNumber ascn = CloudNumber.create(req.getParameter("ascn"));
		byte[] aspk = HexUtil.decodeHex(req.getParameter("aspk"));
		XDIAddress aslc = XDIAddress.create(req.getParameter("aslc"));

		Connection[] connections = CynjaCloudChat.connectionService.viewConnectionsAsChild(parent, child, ascn, aspk, aslc);

		JsonObject jsonObject = JsonUtil.connectionsToJson(connections);

		resp.setContentType("application/json");
		JsonUtil.write(resp.getWriter(), jsonObject);
	}
}
