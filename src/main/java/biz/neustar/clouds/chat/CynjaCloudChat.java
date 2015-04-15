package biz.neustar.clouds.chat;

import biz.neustar.clouds.chat.service.ConnectionService;
import biz.neustar.clouds.chat.service.ParentChildService;
import biz.neustar.clouds.chat.service.stub.StubConnectionService;
import biz.neustar.clouds.chat.service.stub.StubParentChildService;

public class CynjaCloudChat {

	public static ParentChildService parentChildService = new StubParentChildService();
	public static ConnectionService connectionService = new StubConnectionService(parentChildService);
}
