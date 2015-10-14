package biz.neustar.clouds.chat.service.impl.xdi;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.InitFilter;
import biz.neustar.clouds.chat.exceptions.ConnectionNotFoundException;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.ConnectionService;
import xdi2.client.XDIClient;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.instance.ConnectLinkContract;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.SendLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.security.ec25519.signature.create.EC25519StaticPrivateKeySignatureCreator;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.ConnectOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.MessagingResponse;

public class XdiConnectionService implements ConnectionService {

	public static final XDIAddress XDI_ADD_BLOCKED = XDIAddress.create("<#blocked>");

	public static final XDIAddress XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE = XDIAddress.create("*!:uuid:71697b7e-01dc-42e2-9b9f-a9e0a398c6d5#cynja#chat");
	public static final XDIAddress XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO = XDIAddress.create("*!:uuid:71697b7e-01dc-42e2-9b9f-a9e0a398c6d5#cynja#chat{$do}");

	/*	public void connectToChildrenClouds(XDIAddress parent, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + ": " + ex.getMessage(), ex);
		}

		try {

			CloudNumber[] children = CynjaCloudChat.parentChildService.getChildren(parent, ascn, aspk, aslc);

			for (CloudNumber child : children) {

				// create ME from app to child

				Message mAppToChild = mAppToChild(child, ascn, aslc);
				mAppToChild.createConnectOperation(XDIBootstrap.ALL_LINK_CONTRACT_TEMPLATE_ADDRESS);

				// create ME from parent to child

				Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child);
				mParentToChild.createSendOperation(mAppToChild.getMessageEnvelope().getGraph());

				// create ME from app to parent

				Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
				mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

				mSign(mAppToParent, aspk);

				// send

				XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
				client.send(mAppToParent.getMessageEnvelope());
				client.close();

			}

		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot connect to children clouds as parent " + parent + ": " + ex.getMessage(), ex);
		}
	}*/

	@Override
	public Connection requestConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// create ME from child2 to child1

			Message mAppToChild1 = mChildToChildConnect(child2Discovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mAppToChild1.createConnectOperation(XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO);

			// create ME from child1 to child2

			Message mAppToChild2a = mChildToChildConnect(child1Discovery.getCloudNumber(), child2Discovery.getCloudNumber());
			mAppToChild2a.createConnectOperation(XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO);
			Message mAppToChild2b = mChildToChildSend(child1Discovery.getCloudNumber(), child2Discovery.getCloudNumber());
			mAppToChild2b.createSendOperation(mAppToChild1.getMessageEnvelope().getGraph());

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mParentToChild.createSendOperation(mAppToChild2a.getMessageEnvelope().getGraph());
			mParentToChild.createSendOperation(mAppToChild2b.getMessageEnvelope().getGraph());

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			client.send(mAppToParent.getMessageEnvelope());
			client.close();

			//START: To set cloud name as part of connection
			/*			XdiInnerRoot innerRootSet = XdiCommonRoot.findCommonRoot(tempGraph1).getInnerRoot(
					child1Discovery.getCloudNumber().getXDIAddress(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					true);

			innerRootSet.getContextNode().setStatement(XDIStatement.fromComponents(
					child2Discovery.getCloudNumber().getXDIAddress(), 
					XDIDictionaryConstants.XDI_ADD_IS_REF, 
					child2));*/
			//END

			// done

			return XdiConnection.create(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress(), Boolean.FALSE, Boolean.FALSE);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot request connection from child1 " + child1 + " to child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection approveConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// create ME from child2 to child1

			Message mAppToChild1 = mChildToChildConnect(child2Discovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mAppToChild1.createConnectOperation(XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO);

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mParentToChild.createSendOperation(mAppToChild1.getMessageEnvelope().getGraph());

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			client.send(mAppToParent.getMessageEnvelope());
			client.close();

			// done

			return XdiConnection.create(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress(), Boolean.TRUE, Boolean.FALSE);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot request connection from child1 " + child1 + " to child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection[] viewConnectionsAsParent(XDIAddress parent, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + ": " + ex.getMessage(), ex);
		}

		// messages

		try {

			CloudNumber[] children = CynjaCloudChat.parentChildService.getChildren(parent, ascn, aspk, aslc);

			List<Connection> connections = new ArrayList<Connection> ();

			for (CloudNumber child : children) {

				// create ME from parent to child1

				Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child);
				Operation oParentToChild1 = mParentToChild.createGetOperation(XDIAddress.create("[$msg]"));
				oParentToChild1.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEHAS, Boolean.TRUE);
				Operation oParentToChild2 = mParentToChild.createGetOperation(XDIAddress.create("[$do]"));
				oParentToChild2.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEHAS, Boolean.TRUE);

				// create ME from app to parent

				Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
				mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

				mSign(mAppToParent, aspk);

				// send

				Graph resultGraph;

				XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
				resultGraph = client.send(mAppToParent.getMessageEnvelope()).getResultGraph();
				client.close();

				// read result

				MessageEnvelope me = MessageEnvelope.fromGraph(resultGraph);

				for (Message m : me.getMessages()) {

					Iterator<ConnectOperation> operations = m.getConnectOperations();
					if (! operations.hasNext()) continue;
					if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO.equals(operations.next().getTargetXDIAddress())) continue;

					connections.add(XdiConnection.fromMessage(m));
				}

				for (LinkContract linkContract : LinkContracts.getAllLinkContracts(resultGraph)) {

					if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE.equals(((GenericLinkContract) linkContract).getTemplateAuthorityAndId())) continue;

					connections.add(XdiConnection.fromLinkContract((GenericLinkContract) linkContract));
				}
			}

			// done

			return connections.toArray(new Connection[connections.size()]);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view connections as parent " + parent + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection[] viewConnectionsAsChild(XDIAddress parent, XDIAddress child, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult childDiscovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			childDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child);
			if (childDiscovery == null) throw new NullPointerException("Child not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for child " + child + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), childDiscovery.getCloudNumber());
			Operation oParentToChild1 = mParentToChild.createGetOperation(XDIAddress.create("[$msg]"));
			oParentToChild1.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEHAS, Boolean.TRUE);
			Operation oParentToChild2 = mParentToChild.createGetOperation(XDIAddress.create("[$do]"));
			oParentToChild2.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEHAS, Boolean.TRUE);

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			Graph resultGraph;

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			resultGraph = client.send(mAppToParent.getMessageEnvelope()).getResultGraph();
			client.close();

			// read result

			MessageEnvelope me = MessageEnvelope.fromGraph(resultGraph);
			List<Connection> connections = new ArrayList<Connection> ();

			for (Message m : me.getMessages()) {

				Iterator<ConnectOperation> operations = m.getConnectOperations();
				if (! operations.hasNext()) continue;
				if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO.equals(operations.next().getTargetXDIAddress())) continue;

				connections.add(XdiConnection.fromMessage(m));
			}

			for (LinkContract linkContract : LinkContracts.getAllLinkContracts(resultGraph)) {

				if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE.equals(((GenericLinkContract) linkContract).getTemplateAuthorityAndId())) continue;

				connections.add(XdiConnection.fromLinkContract((GenericLinkContract) linkContract));
			}

			// done

			return connections.toArray(new Connection[connections.size()]);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view connections as child " + child  + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Log[] logsConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {


			/*			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createGetOperation(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), parentDiscovery.getCloudNumber().getXDIAddress()));
			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// result

			XDIClient child1Client = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			MessagingResponse mr = child1Client.send(me);

			GenericLinkContract linkContract1 = GenericLinkContract.findGenericLinkContract(
					mr.getGraph(), 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					false);

			if (linkContract1 == null) throw new ConnectionNotFoundException("Connection not found.");
			 */
			// done

			/*		Connection connection = new XdiConnection(linkContract1);

			return CynjaCloudChat.logService.getLogs(connection);*/

			return null;
		} catch (ConnectionNotFoundException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view logs of connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection blockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// prepare target graph

			Graph targetGraph = MemoryGraphFactory.getInstance().openGraph();

			ContextNode linkContractContextNode = targetGraph.setDeepContextNode(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			linkContractContextNode.setDeepContextNode(XDI_ADD_BLOCKED).setLiteralBoolean(Boolean.TRUE);

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mParentToChild.createSetOperation(targetGraph);

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			client.send(mAppToParent.getMessageEnvelope()).getResultGraph();
			client.close();

			// done

			return XdiConnection.create(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress(), Boolean.TRUE, Boolean.TRUE);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot block connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection unblockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// prepare target graph

			Graph targetGraph = MemoryGraphFactory.getInstance().openGraph();

			ContextNode linkContractContextNode = targetGraph.setDeepContextNode(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			linkContractContextNode.setDeepContextNode(XDI_ADD_BLOCKED).setLiteralBoolean(Boolean.FALSE);

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mParentToChild.createSetOperation(targetGraph);

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			client.send(mAppToParent.getMessageEnvelope()).getResultGraph();
			client.close();

			// done

			return XdiConnection.create(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress(), Boolean.TRUE, Boolean.FALSE);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot unblock connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection deleteConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		List<Message> msToDelete = new ArrayList<Message> ();

		try {

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			Operation oParentToChild1 = mParentToChild.createGetOperation(XDIAddress.create("[$msg]"));
			oParentToChild1.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEHAS, Boolean.TRUE);

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			Graph resultGraph;

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			resultGraph = client.send(mAppToParent.getMessageEnvelope()).getResultGraph();
			client.close();

			// read result

			MessageEnvelope me = MessageEnvelope.fromGraph(resultGraph);

			for (Message m : me.getMessages()) {

				Iterator<ConnectOperation> operations = m.getConnectOperations();
				if (! operations.hasNext()) continue;
				if (! XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE_DO.equals(operations.next().getTargetXDIAddress())) continue;

				msToDelete.add(m);
			}
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot retrieve connections to delete as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mParentToChild.createDelOperation(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			for (Message mToDelete : msToDelete) mParentToChild.createDelOperation(mToDelete.getContextNode().getXDIAddress());

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			XDIClient<?> client = new XDIHttpClient(parentDiscovery.getXdiEndpointUri());
			client.send(mAppToParent.getMessageEnvelope());
			client.close();

			// done

			return XdiConnection.create(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress(), Boolean.FALSE, Boolean.FALSE);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot delete connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection findConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// message

		try {

			// create ME from parent to child1

			Message mParentToChild = mParentToChild(parentDiscovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mParentToChild.createGetOperation(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));

			// create ME from app to parent

			Message mAppToParent = mAppToParent(parentDiscovery.getCloudNumber(), ascn, aslc);
			mAppToParent.createSendOperation(mParentToChild.getMessageEnvelope().getGraph());

			mSign(mAppToParent, aspk);

			// send

			XDIClient<?> client = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			MessagingResponse mr = client.send(mAppToParent.getMessageEnvelope());
			client.close();

			// result

			ContextNode linkContractContextNode = mr.getGraph().getDeepContextNode(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			GenericLinkContract linkContract = GenericLinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(linkContractContextNode));
			if (linkContract == null) return null;

			// done

			return XdiConnection.fromLinkContract(linkContract);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot retrieve link contract of child1 " + child1 + ": " + ex.getMessage(), ex);
		}
	}

	/*
	 * Helper methods
	 */

	private static Message mAppToParent(CloudNumber parent, CloudNumber ascn, XDIAddress aslc) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(ascn.getXDIAddress());
		m.setFromPeerRootXDIArc(ascn.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(parent.getPeerRootXDIArc());
		m.setLinkContractXDIAddress(aslc);

		return m;
	}

	private static Message mParentToChild(CloudNumber parent, CloudNumber child) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(parent.getXDIAddress());
		m.setFromPeerRootXDIArc(parent.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child.getPeerRootXDIArc());
		m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child.getXDIAddress(), parent.getXDIAddress()));

		return m;
	}

	private static Message mChildToChildConnect(CloudNumber child1, CloudNumber child2) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(child1.getXDIAddress());
		m.setFromPeerRootXDIArc(child1.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child2.getPeerRootXDIArc());
		m.setLinkContractClass(ConnectLinkContract.class);

		return m;
	}

	private static Message mChildToChildSend(CloudNumber child1, CloudNumber child2) {

		MessageEnvelope me = new MessageEnvelope();
		Message m = me.createMessage(child1.getXDIAddress());
		m.setFromPeerRootXDIArc(child1.getPeerRootXDIArc());
		m.setToPeerRootXDIArc(child2.getPeerRootXDIArc());
		m.setLinkContractClass(SendLinkContract.class);

		return m;
	}

	private static void mSign(Message m, byte[] aspk) throws GeneralSecurityException {

		new EC25519StaticPrivateKeySignatureCreator(aspk).createSignature(m.getContextNode());
	}

	private static XDIAddress chatLinkContractXDIAddress(XDIAddress child1, XDIAddress child2) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child1, child2, XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE);
	}

	private static XDIAddress dependentLinkContractXDIAddress(XDIAddress child, XDIAddress parent) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child, parent, null);
	}
}
