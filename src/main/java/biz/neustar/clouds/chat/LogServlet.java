package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.util.JsonUtil;

import com.google.gson.JsonArray;

public class LogServlet extends HttpServlet {

	private static final long serialVersionUID = 2806072987404647289L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		XDIAddress child1 = XDIAddress.create(req.getParameter("child1"));
		XDIAddress child2 = XDIAddress.create(req.getParameter("child2"));

		Log[] logs = CynjaCloudChat.connectionService.viewConnectionLogs(parent, child1, child2);

		JsonArray jsonArray = new JsonArray();

		for (Log log : logs) {

			jsonArray.add(JsonUtil.logToJson(log));
		}

		resp.setContentType("appliction/json");
		JsonUtil.write(resp.getWriter(), jsonArray);
	}
}
