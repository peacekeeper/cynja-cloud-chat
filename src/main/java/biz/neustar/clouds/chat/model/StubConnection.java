package biz.neustar.clouds.chat.model;

import xdi2.core.syntax.XDIAddress;

public class StubConnection implements Connection {

	private XDIAddress child1, child2;
	private boolean approved1, approved2;
	private boolean blocked1, blocked2;

	public StubConnection(XDIAddress child1, XDIAddress child2) {

		this.child1 = child1;
		this.child2 = child2;
		this.approved1 = false;
		this.approved2 = false;
		this.blocked1 = false;
		this.blocked2 = false;
	}

	public XDIAddress getChild1() {
		return child1;
	}
	public void setChild1(XDIAddress child1) {
		this.child1 = child1;
	}
	public XDIAddress getChild2() {
		return child2;
	}
	public void setChild2(XDIAddress child2) {
		this.child2 = child2;
	}
	public boolean isApproved1() {
		return approved1;
	}
	public void setApproved1(boolean approved1) {
		this.approved1 = approved1;
	}
	public boolean isApproved2() {
		return approved2;
	}
	public void setApproved2(boolean approved2) {
		this.approved2 = approved2;
	}
	public boolean isBlocked1() {
		return blocked1;
	}
	public void setBlocked1(boolean blocked1) {
		this.blocked1 = blocked1;
	}
	public boolean isBlocked2() {
		return blocked2;
	}
	public void setBlocked2(boolean blocked2) {
		this.blocked2 = blocked2;
	}
}
