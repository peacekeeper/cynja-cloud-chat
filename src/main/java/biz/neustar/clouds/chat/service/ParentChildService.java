package biz.neustar.clouds.chat.service;

import xdi2.core.syntax.XDIAddress;

public interface ParentChildService {

	public boolean isParent(XDIAddress parent, String parentSecretToken, XDIAddress child);
	public XDIAddress[] getChildren(XDIAddress parent, String parentSecretToken);
}
