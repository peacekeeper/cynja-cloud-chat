package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.syntax.XDIAddress;

public class DeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 2524699264338535286L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		String parentSecretToken = req.getParameter("parentSecretToken");
		XDIAddress child1 = XDIAddress.create(req.getParameter("child1"));
		XDIAddress child2 = XDIAddress.create(req.getParameter("child2"));

		CynjaCloudChat.connectionService.deleteConnection(parent, parentSecretToken, child1, child2);

/*		for (Session session : connection.getSessions()) { 

			session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Connection has been deleted."));
		}*/
	}
}
