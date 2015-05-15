package biz.neustar.clouds.chat.service;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;

public interface ConnectionService {

	public Connection requestConnection(XDIAddress child1, String child1SecretToken, XDIAddress child2);
	public Connection approveConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2);
	public Connection[] viewConnectionsAsParent(XDIAddress parent, String parentSecretToken);
	public Connection[] viewConnectionsAsChild(XDIAddress child, String childSecretToken);
	public Log[] logsConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2);
	public Connection blockConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2);
	public Connection unblockConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2);
	public Connection deleteConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2);
	public Connection findConnection(XDIAddress child1, String child1SecretToken, XDIAddress child2);
}
