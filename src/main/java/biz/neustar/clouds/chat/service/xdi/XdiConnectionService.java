package biz.neustar.clouds.chat.service.xdi;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.ConnectionService;

public class XdiConnectionService implements ConnectionService {

	@Override
	public Connection requestConnection(XDIAddress initiatingChild, XDIAddress targetChild) {

		return null;
	}

	@Override
	public Connection findConnection(XDIAddress initiatingChild, XDIAddress targetChild) {

		return null;
	}

	@Override
	public Connection[] viewConnections(XDIAddress parentOrChild) {

		return null;
	}

	@Override
	public Log[] viewConnectionLogs(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		return null;
	}

	@Override
	public Connection approveConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

		return null;
	}

	@Override
	public Connection blockConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

		return null;
	}

	@Override
	public Connection unblockConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

		return null;
	}

	@Override
	public Connection deleteConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

		return null;
	}
}
