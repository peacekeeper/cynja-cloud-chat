package biz.neustar.clouds.chat.service.impl.xdi;

import xdi2.core.Literal;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.model.Connection;

public class XdiConnection implements Connection {

	public static final int MAX_LOG_SIZE = 20;

	public static final XDIAddress XDI_ADD_APPROVED = XDIAddress.create("<#approved>");
	public static final XDIAddress XDI_ADD_BLOCKED = XDIAddress.create("<#blocked>");

	private GenericLinkContract linkContract1;
	private GenericLinkContract linkContract2;

	XdiConnection(GenericLinkContract linkContract1, GenericLinkContract linkContract2) {

		this.linkContract1 = linkContract1;
		this.linkContract2 = linkContract2;
	}

	public GenericLinkContract getLinkContract1() {

		return this.linkContract1;
	}

	public GenericLinkContract getLinkContract2() {

		return this.linkContract2;
	}

	@Override
	public XDIAddress getChild1() {

		return this.linkContract1.getAuthorizingAuthority();
	}

	@Override
	public XDIAddress getChild2() {

		return this.linkContract1.getRequestingAuthority();
	}

	@Override
	public Boolean isApproved1() {

		XdiAttribute xdiAttribute = this.linkContract1.getXdiEntity().getXdiAttribute(XDI_ADD_APPROVED, false);
		XdiValue xdiValue = xdiAttribute == null ? null : xdiAttribute.getXdiValue(false);
		Literal literal = xdiValue == null ? null : xdiValue.getLiteral();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public Boolean isApproved2() {

		if (this.linkContract2 == null) return null;

		XdiAttribute xdiAttribute = this.linkContract2.getXdiEntity().getXdiAttribute(XDI_ADD_APPROVED, false);
		XdiValue xdiValue = xdiAttribute == null ? null : xdiAttribute.getXdiValue(false);
		Literal literal = xdiValue == null ? null : xdiValue.getLiteral();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public Boolean isBlocked1() {

		XdiAttribute xdiAttribute = this.linkContract1.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, false);
		XdiValue xdiValue = xdiAttribute == null ? null : xdiAttribute.getXdiValue(false);
		Literal literal = xdiValue == null ? null : xdiValue.getLiteral();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public Boolean isBlocked2() {

		if (this.linkContract2 == null) return null;

		XdiAttribute xdiAttribute = this.linkContract2.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, false);
		XdiValue xdiValue = xdiAttribute == null ? null : xdiAttribute.getXdiValue(false);
		Literal literal = xdiValue == null ? null : xdiValue.getLiteral();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}
}
