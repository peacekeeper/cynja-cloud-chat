package biz.neustar.clouds.chat.service;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public interface ParentChildService {

	public boolean isParent(XDIAddress parent, XDIAddress child, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
	public CloudNumber[] getChildren(XDIAddress parent, CloudNumber ascn, byte[] aspk, XDIAddress aslc);
}
