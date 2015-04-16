package biz.neustar.clouds.chat.model;

import xdi2.core.syntax.XDIAddress;

public interface Connection {

	public XDIAddress getChild1();
	public XDIAddress getChild2();
	public boolean isApproved1();
	public boolean isApproved2();
	public boolean isBlocked1();
	public boolean isBlocked2();
	public void addLog(String line);
	public Log[] viewLog();
}
