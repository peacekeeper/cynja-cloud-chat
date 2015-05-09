package biz.neustar.clouds.chat.service;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;

public interface ConnectionService {

	public Connection requestConnection(XDIAddress child1, XDIAddress child2);
	public Connection findConnection(XDIAddress child1, XDIAddress child2);
	public Connection[] viewConnections(XDIAddress parentOrChild);
	public Log[] viewConnectionLogs(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public Connection approveConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public Connection blockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public Connection unblockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
	public Connection deleteConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2);
}
