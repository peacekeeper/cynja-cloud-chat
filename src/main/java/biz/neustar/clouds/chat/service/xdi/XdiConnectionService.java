package biz.neustar.clouds.chat.service.xdi;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.ConnectionService;

public class XdiConnectionService implements ConnectionService {

	@Override
	public Connection establishConnection(XDIAddress initiatingChild, XDIAddress targetChild) {

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
	public Log[] viewConnectionLog(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		return null;
	}

	@Override
	public void approveConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

	}

	@Override
	public void blockConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

	}

	@Override
	public void unblockConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

	}

	@Override
	public void deleteConnection(XDIAddress parent, XDIAddress initiatingChild, XDIAddress targetChild) {

	}
}
