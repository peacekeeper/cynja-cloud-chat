package biz.neustar.clouds.chat.service.impl.stub;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;

public class StubConnection implements Connection {

	private XDIAddress child1, child2;
	private Boolean approved1, approved2;
	private Boolean blocked1, blocked2;

	StubConnection(XDIAddress child1, XDIAddress child2) {

		this.child1 = child1;
		this.child2 = child2;
		this.approved1 = false;
		this.approved2 = false;
		this.blocked1 = false;
		this.blocked2 = false;
	}

	void setApproved1(Boolean approved1) {

		this.approved1 = approved1;
	}

	void setApproved2(Boolean approved2) {

		this.approved2 = approved2;
	}

	void setBlocked1(Boolean blocked1) {

		this.blocked1 = blocked1;
	}

	void setBlocked2(Boolean blocked2) {

		this.blocked2 = blocked2;
	}

	public XDIAddress getChild1() {

		return this.child1;
	}

	public XDIAddress getChild2() {

		return this.child2;
	}

	public Boolean isApproved1() {

		return this.approved1;
	}

	public Boolean isApproved2() {

		return this.approved2;
	}

	public Boolean isBlocked1() {

		return this.blocked1;
	}

	public Boolean isBlocked2() {

		return this.blocked2;
	}
}
