package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.util.HexUtil;

public class DeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 2524699264338535286L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		XDIAddress child1 = XDIAddress.create(req.getParameter("child1"));
		XDIAddress child2 = XDIAddress.create(req.getParameter("child2"));
		CloudNumber ascn = CloudNumber.create(req.getParameter("ascn"));
		byte[] aspk = HexUtil.decodeHex(req.getParameter("aspk"));
		XDIAddress aslc = XDIAddress.create(req.getParameter("aslc"));

		Connection connection = CynjaCloudChat.connectionService.deleteConnection(parent, child1, child2, ascn, aspk, aslc);

		Session[] sessions = CynjaCloudChat.sessionService.getSessions(connection);

		for (Session session : sessions) { 

			session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Connection has been deleted."));
		}
	}
}
