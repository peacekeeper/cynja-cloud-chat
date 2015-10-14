package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biz.neustar.clouds.chat.util.HexUtil;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public class ConnectToChildrenCloudsServlet extends HttpServlet {

	private static final long serialVersionUID = 8396021897147651818L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		XDIAddress parent = XDIAddress.create(req.getParameter("parent"));
		CloudNumber ascn = CloudNumber.create(req.getParameter("ascn"));
		byte[] aspk = HexUtil.decodeHex(req.getParameter("aspk"));
		XDIAddress aslc = XDIAddress.create(req.getParameter("aslc"));

//		CynjaCloudChat.connectionService.connectToChildrenClouds(parent, ascn, aspk, aslc);
	}
}
