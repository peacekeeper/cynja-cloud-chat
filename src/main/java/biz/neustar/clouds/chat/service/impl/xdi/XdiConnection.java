package biz.neustar.clouds.chat.service.impl.xdi;

import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.service.impl.AbstractConnection;
import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class XdiConnection extends AbstractConnection 	implements Connection {

	public static final XDIAddress XDI_ADD_BLOCKED = XDIAddress.create("<#blocked>");

	private XdiConnection(XDIAddress child1, XDIAddress child2, Boolean approve1, Boolean blocked1, CloudName connectionName) {

		super(child1, child2, approve1, blocked1, connectionName);
	}

	public static XdiConnection fromLinkContract(GenericLinkContract linkContract) {

		XdiAttribute xdiAttribute = linkContract.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, false);
		LiteralNode literal = xdiAttribute == null ? null : xdiAttribute.getLiteralNode();
		Boolean blocked1 = literal == null ? null : literal.getLiteralDataBoolean();

		ContextNode requestingAuthorityContextNode = linkContract.getContextNode().getDeepContextNode(linkContract.getRequestingAuthority());
		Relation requestingAuthorityRelation = requestingAuthorityContextNode == null ? null : requestingAuthorityContextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_REF);
		CloudName connectionName = requestingAuthorityRelation == null ? null : CloudName.fromXDIAddress(requestingAuthorityRelation.getTargetXDIAddress());

		return new XdiConnection(
				linkContract.getAuthorizingAuthority(),
				linkContract.getRequestingAuthority(),
				Boolean.TRUE,
				blocked1,
				connectionName);
	}

	public static XdiConnection fromMessage(Message message) {

		return new XdiConnection(
				message.getToXDIAddress(),
				message.getSenderXDIAddress(),
				Boolean.FALSE,
				Boolean.FALSE,
				null);
	}
	
	public static XdiConnection create(XDIAddress child1, XDIAddress child2, Boolean approve1, Boolean approve2) {
		
		return new XdiConnection(
				child1,
				child2,
				approve1,
				approve2,
				null);
	}
}
