package biz.neustar.clouds.chat.service.impl.xdi;

import biz.neustar.clouds.chat.InitFilter;
import biz.neustar.clouds.chat.exceptions.NotParentOfChildException;
import biz.neustar.clouds.chat.service.ParentChildService;
import xdi2.client.XDIClient;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.security.ec25519.signature.create.EC25519StaticPrivateKeySignatureCreator;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingCloudNumberIterator;
import xdi2.core.util.iterators.MappingRelationTargetXDIAddressIterator;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public class XdiParentChildService implements ParentChildService {

	public static final XDIAddress XDI_ADD_IS_GUARDIAN = XDIAddress.create("$is#guardian");

	@Override
	public boolean isParent(XDIAddress parent, XDIAddress child, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		try {

			// discovery

			XDIDiscoveryResult parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			XDIDiscoveryResult childDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child);

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(ascn.getXDIAddress());
			m.createGetOperation(XDIStatement.fromRelationComponents(parentDiscovery.getCloudNumber().getXDIAddress(), XDI_ADD_IS_GUARDIAN, childDiscovery.getCloudNumber().getXDIAddress()));
			m.setToXDIAddress(parentDiscovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(aslc);
			new EC25519StaticPrivateKeySignatureCreator(aspk).createSignature(m.getContextNode());

			XDIClient<?> parentClient = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			MessagingResponse mr = parentClient.send(me);
			parentClient.close();

			// result

			boolean result = mr.getGraph().isEmpty();

			// done

			return result;
		} catch (Exception ex) {

			throw new NotParentOfChildException("Cannot determine whether " + parent + " is a parent of child " + child + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public CloudNumber[] getChildren(XDIAddress parent, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		try {

			// discovery

			XDIDiscoveryResult parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			System.err.println(parentDiscovery);

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(ascn.getXDIAddress());
			m.createGetOperation(XDIStatement.fromRelationComponents(parentDiscovery.getCloudNumber().getXDIAddress(), XDI_ADD_IS_GUARDIAN, XDIConstants.XDI_ADD_COMMON_VARIABLE));
			m.setToXDIAddress(parentDiscovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(aslc);
			new EC25519StaticPrivateKeySignatureCreator(aspk).createSignature(m.getContextNode());

			XDIClient<?> parentClient = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			MessagingResponse mr = parentClient.send(me);
			parentClient.close();

			// result

			CloudNumber[] result;

			ContextNode parentContextNode = mr.getGraph().getDeepContextNode(parentDiscovery.getCloudNumber().getXDIAddress());
			result = parentContextNode == null ? 
					new CloudNumber[0] : 
						new IteratorArrayMaker<CloudNumber> (
								new MappingCloudNumberIterator(
								new MappingRelationTargetXDIAddressIterator(
										parentContextNode.getRelations(XDI_ADD_IS_GUARDIAN)))).array(CloudNumber.class);

			// done

			return result;
		} catch (Exception ex) {

			throw new NotParentOfChildException("Cannot get children of parent " + parent + ": " + ex.getMessage(), ex);
		}
	}
}
