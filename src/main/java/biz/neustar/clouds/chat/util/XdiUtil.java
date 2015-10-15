package biz.neustar.clouds.chat.util;

import java.security.GeneralSecurityException;
import java.util.Iterator;

import xdi2.core.features.linkcontracts.instance.ConnectLinkContract;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.SendLinkContract;
import xdi2.core.security.ec25519.signature.create.EC25519StaticPrivateKeySignatureCreator;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.ConnectOperation;

public class XdiUtil {

	public static final XDIAddress XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE = XDIAddress.create("*!:uuid:71697b7e-01dc-42e2-9b9f-a9e0a398c6d5#cynja#chat");
	public static final XDIAddress XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO = XDIAddress.create("*!:uuid:71697b7e-01dc-42e2-9b9f-a9e0a398c6d5#cynja#chat{$do}");

	public static Message mAppToParent(CloudNumber parent, CloudNumber ascn, XDIAddress aslc) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(ascn.getXDIAddress());
		m.setFromPeerRootXDIArc(ascn.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(parent.getPeerRootXDIArc());
		m.setLinkContractXDIAddress(aslc);

		return m;
	}

	public static Message mAppToChild(CloudNumber child, CloudNumber ascn, XDIAddress caslc) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(ascn.getXDIAddress());
		m.setFromPeerRootXDIArc(ascn.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child.getPeerRootXDIArc());
		m.setLinkContractXDIAddress(caslc);

		return m;
	}

	public static Message mParentToChild(CloudNumber parent, CloudNumber child) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(parent.getXDIAddress());
		m.setFromPeerRootXDIArc(parent.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child.getPeerRootXDIArc());
		m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child.getXDIAddress(), parent.getXDIAddress()));

		return m;
	}

	public static Message mChildToChildChat(CloudNumber child1, CloudNumber child2) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(child1.getXDIAddress());
		m.setFromPeerRootXDIArc(child1.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child2.getPeerRootXDIArc());
		m.setLinkContractXDIAddress(chatLinkContractXDIAddress(child2.getXDIAddress(), child1.getXDIAddress()));

		return m;
	}

	public static Message mChildToChildConnect(CloudNumber child1, CloudNumber child2) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(child1.getXDIAddress());
		m.setFromPeerRootXDIArc(child1.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child2.getPeerRootXDIArc());
		m.setLinkContractClass(ConnectLinkContract.class);

		return m;
	}

	public static Message mChildToChildSend(CloudNumber child1, CloudNumber child2) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(child1.getXDIAddress());
		m.setFromPeerRootXDIArc(child1.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child2.getPeerRootXDIArc());
		m.setLinkContractClass(SendLinkContract.class);

		return m;
	}

	public static void mSign(Message m, byte[] aspk) throws GeneralSecurityException {

		new EC25519StaticPrivateKeySignatureCreator(aspk).createSignature(m.getContextNode());
	}

	public static boolean isCynjaChatConnectM(Message m) {

		Iterator<ConnectOperation> operations = m.getConnectOperations();
		if (! operations.hasNext()) return false;
		if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO.equals(operations.next().getTargetXDIAddress())) return false;

		return true;
	}

	public static boolean isCynjaChatLC(LinkContract lc) {

		if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE.equals(((GenericLinkContract) lc).getTemplateAuthorityAndId())) return false;

		return true;
	}

	public static XDIAddress chatLinkContractXDIAddress(XDIAddress child1, XDIAddress child2) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child1, child2, XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE);
	}

	public static XDIAddress dependentLinkContractXDIAddress(XDIAddress child, XDIAddress parent) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child, parent, null);
	}
}
