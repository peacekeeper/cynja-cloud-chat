package biz.neustar.clouds.chat.model;

import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;

public interface Connection {

	public XDIAddress getChild1();
	public XDIAddress getChild2();
	public Boolean isApproved1();
	public Boolean isApproved2();
	public Boolean isBlocked1();
	public Boolean isBlocked2();
	public CloudName getConnectionName();
}
