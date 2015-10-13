package biz.neustar.clouds.chat.service.impl.xdi;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Connection;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class XdiConnection implements Connection {

	public static final int MAX_LOG_SIZE = 20;

	public static final XDIArc XDI_ARC_CHAT = XDIArc.create("#chat");
	public static final XDIAddress XDI_ADD_CHAT = XDIAddress.create("#chat");

	public static final XDIAddress XDI_ADD_APPROVED = XDIAddress.create("<#approved>");
	public static final XDIAddress XDI_ADD_BLOCKED = XDIAddress.create("<#blocked>");

	private GenericLinkContract linkContract1;
	private GenericLinkContract linkContract2;

	XdiConnection(GenericLinkContract linkContract1, GenericLinkContract linkContract2) {

		this.linkContract1 = linkContract1;
		this.linkContract2 = linkContract2;
	}

	XdiConnection(GenericLinkContract linkContract1) {

		this.linkContract1 = linkContract1;
		this.linkContract2 = null;
	}

	XdiConnection(XDIAddress child1, XDIAddress child2) {

		Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();

		GenericLinkContract linkContract1 = GenericLinkContract.findGenericLinkContract(
				tempGraph, 
				child1, 
				child2, 
				XDI_ADD_CHAT, 
				null,
				true);

		this.linkContract1 = linkContract1;
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
		LiteralNode literal = xdiAttribute == null ? null : xdiAttribute.getLiteralNode();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public Boolean isApproved2() {
		if (this.linkContract2 == null) return null;
		XdiAttribute xdiAttribute = this.linkContract2.getXdiEntity().getXdiAttribute(XDI_ADD_APPROVED, false);
		LiteralNode literal = xdiAttribute == null ? null : xdiAttribute.getLiteralNode();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public Boolean isBlocked1() {
		XdiAttribute xdiAttribute = this.linkContract1.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, false);
		LiteralNode literal = xdiAttribute == null ? null : xdiAttribute.getLiteralNode();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public Boolean isBlocked2() {
		if (this.linkContract2 == null) return null;
		XdiAttribute xdiAttribute = this.linkContract2.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, false);
		LiteralNode literal = xdiAttribute == null ? null : xdiAttribute.getLiteralNode();

		return literal == null ? null : literal.getLiteralDataBoolean();
	}

	@Override
	public CloudName getConnectionName() {

		ContextNode requestingAuthorityContextNode = linkContract1.getContextNode().getDeepContextNode(linkContract1.getRequestingAuthority());
		if (requestingAuthorityContextNode == null) return null;

		Relation requestingAuthorityRelation = requestingAuthorityContextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_REF);
		if (requestingAuthorityRelation == null) return null;

		return CloudName.fromXDIAddress(requestingAuthorityRelation.getTargetXDIAddress());
	}
}
