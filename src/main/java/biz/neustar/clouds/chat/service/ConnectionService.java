package biz.neustar.clouds.chat.service;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;

public interface ConnectionService {

	public Connection establishConnection(XDIAddress child1, XDIAddress child2);
	public Connection findConnection(XDIAddress child1, XDIAddress child2);
	public Connection[] viewConnections(XDIAddress parentOrChild);
	public Log[] viewConnectionLog(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public void approveConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public void blockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public void unblockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public void deleteConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
}
