package biz.neustar.clouds.chat.service;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public interface ConnectionService {

	public XDIAddress connectToChildCloud(XDIAddress parent, XDIAddress child, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection requestConnection(XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress caslc);
	public Connection approveConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection[] viewConnectionsAsParent(XDIAddress parent, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection[] viewConnectionsAsChild(XDIAddress child, CloudNumber ascn, byte[] aspk, XDIAddress caslc);
	public Log[] logsConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection blockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection unblockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection deleteConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public Connection findConnection(XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress caslc);
}
