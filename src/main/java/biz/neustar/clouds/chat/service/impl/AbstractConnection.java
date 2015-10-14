package biz.neustar.clouds.chat.service.impl;

import biz.neustar.clouds.chat.model.Connection;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;

public abstract class AbstractConnection implements Connection {

	private XDIAddress child1;
	private XDIAddress child2;
	private Boolean approved1;
	private Boolean blocked1;
	private CloudName connectionName;

	public AbstractConnection(XDIAddress child1, XDIAddress child2, Boolean approve1, Boolean blocked1, CloudName connectionName) {

		this.child1 = child1;
		this.child2 = child2;
		this.approved1 = approve1;
		this.blocked1 = blocked1;
		this.connectionName = connectionName;
	}

	public XDIAddress getChild1() {
		return child1;
	}

	public XDIAddress getChild2() {
		return child2;
	}

	public Boolean isApproved1() {
		return approved1;
	}

	public Boolean isBlocked1() {
		return blocked1;
	}

	public CloudName getConnectionName() {
		return connectionName;
	}

	

	public Boolean isApproved2() {
		return Boolean.TRUE;
	}

	public Boolean isBlocked2() {
		return Boolean.FALSE;
	}
}
