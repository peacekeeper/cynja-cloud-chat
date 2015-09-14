package biz.neustar.clouds.chat.service.impl.xdi;

import biz.neustar.clouds.chat.InitFilter;
import biz.neustar.clouds.chat.exceptions.NotParentOfChildException;
import biz.neustar.clouds.chat.service.ParentChildService;
import xdi2.client.XDIClient;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingRelationTargetXDIAddressIterator;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public class XdiParentChildService implements ParentChildService {

	public static final XDIAddress XDI_ADD_IS_GUARDIAN = XDIAddress.create("$is#guardian");

	@Override
	public boolean isParent(XDIAddress parent, String parentSecretToken, XDIAddress child) {

		try {

			// discovery

			XDIDiscoveryResult parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			XDIDiscoveryResult childDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child);

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createGetOperation(XDIStatement.fromRelationComponents(parentDiscovery.getCloudNumber().getXDIAddress(), XDI_ADD_IS_GUARDIAN, childDiscovery.getCloudNumber().getXDIAddress()));
			m.setToXDIAddress(parentDiscovery.getCloudNumber().getXDIAddress());
			m.setLinkContractClass(RootLinkContract.class);
			m.setSecretToken(parentSecretToken);

			XDIClient parentClient = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			MessagingResponse mr = parentClient.send(me);

			// result

			boolean result = mr.getGraph().isEmpty();

			// done

			return result;
		} catch (Exception ex) {

			throw new NotParentOfChildException("Cannot determine whether " + parent + " is a parent of child " + child + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public XDIAddress[] getChildren(XDIAddress parent, String parentSecretToken) {

		try {

			// discovery

			XDIDiscoveryResult parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createGetOperation(XDIStatement.fromRelationComponents(parentDiscovery.getCloudNumber().getXDIAddress(), XDI_ADD_IS_GUARDIAN, XDIConstants.XDI_ADD_COMMON_VARIABLE));
			m.setToXDIAddress(parentDiscovery.getCloudNumber().getXDIAddress());
			m.setLinkContractClass(RootLinkContract.class);
			m.setSecretToken(parentSecretToken);

			XDIClient parentClient = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			MessagingResponse mr = parentClient.send(me);

			// result

			XDIAddress[] result;

			ContextNode parentContextNode = mr.getGraph().getDeepContextNode(parentDiscovery.getCloudNumber().getXDIAddress());
			result = parentContextNode == null ? new XDIAddress[0] : new IteratorArrayMaker<XDIAddress> (new MappingRelationTargetXDIAddressIterator(parentContextNode.getRelations(XDI_ADD_IS_GUARDIAN))).array(XDIAddress.class);

			// done

			return result;
		} catch (Exception ex) {

			throw new NotParentOfChildException("Cannot get children of parent " + parent + ": " + ex.getMessage(), ex);
		}
	}
}
