package biz.neustar.clouds.chat;

import java.io.IOException;
import java.text.DateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import biz.neustar.clouds.chat.util.HexUtil;
import biz.neustar.clouds.chat.util.JsonUtil;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public class ConnectToChildCloudServlet extends HttpServlet {

	private static final long serialVersionUID = 8396021897147651818L;

	private static final Gson gson = new GsonBuilder()
			.setDateFormat(DateFormat.FULL, DateFormat.FULL)
			.disableHtmlEscaping()
			.serializeNulls()
			.create();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		XDIAddress child = XDIAddress.create(req.getParameter("child"));
		CloudNumber ascn = CloudNumber.create(req.getParameter("ascn"));
		byte[] aspk = HexUtil.decodeHex(req.getParameter("aspk"));
		XDIAddress aslc = XDIAddress.create(req.getParameter("aslc"));

		XDIAddress caslc = CynjaCloudChat.connectionService.connectToChildCloud(parent, child, ascn, aspk, aslc);

		// response

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("aslc", gson.toJsonTree(caslc.toString()));

		resp.setContentType("application/json");
		JsonUtil.write(resp.getWriter(), jsonObject);
	}
}
