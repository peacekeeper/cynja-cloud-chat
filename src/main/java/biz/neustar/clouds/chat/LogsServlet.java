package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.util.HexUtil;
import biz.neustar.clouds.chat.util.JsonUtil;

import com.google.gson.JsonArray;

public class LogsServlet extends HttpServlet {

	private static final long serialVersionUID = 2806072987404647289L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		XDIAddress child1 = XDIAddress.create(req.getParameter("child1"));
		XDIAddress child2 = XDIAddress.create(req.getParameter("child2"));
		CloudNumber ascn = CloudNumber.create(req.getParameter("ascn"));
		byte[] aspk = HexUtil.decodeHex(req.getParameter("aspk"));
		XDIAddress aslc = XDIAddress.create(req.getParameter("aslc"));

		Log[] logs = CynjaCloudChat.connectionService.logsConnection(parent, child1, child2, ascn, aspk, aslc);

		JsonArray jsonArray = new JsonArray();
		for (Log log : logs) jsonArray.add(JsonUtil.logToJson(log));

		resp.setContentType("application/json");
		JsonUtil.write(resp.getWriter(), jsonArray);
	}
}
