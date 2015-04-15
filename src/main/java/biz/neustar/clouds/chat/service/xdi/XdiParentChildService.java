package biz.neustar.clouds.chat.service.xdi;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.service.ParentChildService;

public class XdiParentChildService implements ParentChildService {

	@Override
	public boolean isParent(XDIAddress parent, XDIAddress child) {

		return false;
	}
}
