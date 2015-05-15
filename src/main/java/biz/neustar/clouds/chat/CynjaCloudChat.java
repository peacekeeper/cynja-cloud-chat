package biz.neustar.clouds.chat;

import biz.neustar.clouds.chat.service.ConnectionService;
import biz.neustar.clouds.chat.service.LogService;
import biz.neustar.clouds.chat.service.ParentChildService;
import biz.neustar.clouds.chat.service.SessionService;
import biz.neustar.clouds.chat.service.impl.DefaultLogService;
import biz.neustar.clouds.chat.service.impl.DefaultSessionService;
import biz.neustar.clouds.chat.service.impl.xdi.XdiConnectionService;
import biz.neustar.clouds.chat.service.impl.xdi.XdiParentChildService;

public class CynjaCloudChat {

	public static ParentChildService parentChildService = new XdiParentChildService();
	public static ConnectionService connectionService = new XdiConnectionService();
	public static SessionService sessionService = new DefaultSessionService();
	public static LogService logService = new DefaultLogService();
}
