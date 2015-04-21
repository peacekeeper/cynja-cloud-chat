package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;

public class BlockServlet extends HttpServlet {

	private static final long serialVersionUID = 2049298539409005496L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		XDIAddress child1 = XDIAddress.create(req.getParameter("child1"));
		XDIAddress child2 = XDIAddress.create(req.getParameter("child2"));

		Connection connection = CynjaCloudChat.connectionService.blockConnection(parent, child1, child2);

		for (Session session : connection.getSessions()) { 

			session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Connection has been blocked."));
		}
	}
}
