package biz.neustar.clouds.chat.service.impl.xdi;

import java.security.GeneralSecurityException;

import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.InitFilter;
import biz.neustar.clouds.chat.exceptions.ConnectionNotFoundException;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.ConnectionService;
import xdi2.client.XDIClient;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instance.ConnectLinkContract;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.linkcontracts.instance.SendLinkContract;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.security.ec25519.signature.create.EC25519StaticPrivateKeySignatureCreator;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public class XdiConnectionService implements ConnectionService {

	public static final XDIArc XDI_ARC_CHAT = XDIArc.create("#chat");
	public static final XDIAddress XDI_ADD_CHAT = XDIAddress.create("#chat");

	public static final XDIAddress XDI_ADD_APPROVED = XDIAddress.create("<#approved>");
	public static final XDIAddress XDI_ADD_BLOCKED = XDIAddress.create("<#blocked>");

	public static final XDIAddress XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE = XDIAddress.create("*!:uuid:71697b7e-01dc-42e2-9b9f-a9e0a398c6d5#child#chat{$do}");

	public void connectToChildrenClouds(XDIAddress parent, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

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

				Message mAppToChild = null/*mAppToChild(child, ascn, aslc)*/;
				//mAppToChild.createConnectOperation(XDIBootstrap.ALL_LINK_CONTRACT_TEMPLATE_ADDRESS);

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
	}

	@Override
	public void requestConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

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

		try {

			// create ME from child2 to child1

			Message mAppToChild1 = mChildToChildConnect(child2Discovery.getCloudNumber(), child1Discovery.getCloudNumber());
			mAppToChild1.createConnectOperation(XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE);
			
			// create ME from child1 to child2

			Message mAppToChild2a = mChildToChildConnect(child1Discovery.getCloudNumber(), child2Discovery.getCloudNumber());
			mAppToChild2a.createConnectOperation(XDI_ADD_CYNJA_CHAT_LINK_CONTRACT_TEMPLATE);
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

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		try {

			// done

			return null;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot approve connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
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

		try {
			/*
			XDIAddress[] children = CynjaCloudChat.parentChildService.getChildren(parent, parentSecretToken);

			List<Connection> connections = new ArrayList<Connection> ();

			for (XDIAddress child : children) {

				XDIDiscoveryResult childDiscovery;

				// discovery

				childDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child);

				// message

				MessageEnvelope me = new MessageEnvelope();
				Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
				m.createGetOperation(XDIAddressUtil.concatXDIAddresses(
						childDiscovery.getCloudNumber().getXDIAddress(),
						XDI_ADD_CHAT_DO_EC));
				m.setToXDIAddress(childDiscovery.getCloudNumber().getXDIAddress());
				m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(childDiscovery.getCloudNumber().getXDIAddress(), parentDiscovery.getCloudNumber().getXDIAddress()));
				new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

				XDIClient childClient = new XDIHttpClient(childDiscovery.getXdiEndpointUri());
				MessagingResponse mr = childClient.send(me);

				// result

				XdiEntityCollection linkContractXdiEntityCollection = XdiCommonRoot.findCommonRoot(mr.getGraph()).
						getXdiEntityCollection(
								XDIAddressUtil.concatXDIAddresses(
										childDiscovery.getCloudNumber().getXDIAddress(),
										XDI_ADD_CHAT_DO_EC),
								false);

				if (linkContractXdiEntityCollection == null) continue;

				for (XdiEntityInstance xdiEntityInstance : linkContractXdiEntityCollection.getXdiInstancesUnordered()) {

					XdiEntity xdiEntity = xdiEntityInstance.dereference();
					GenericLinkContract linkContract = GenericLinkContract.fromXdiEntity(xdiEntity);

					if (linkContract == null) continue;

					connections.add(new XdiConnection(linkContract, null, mr.getGraph()));
				}
			}*/

			// done

			//return connections.toArray(new Connection[connections.size()]);
			return null;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view connections as parent " + parent + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection[] viewConnectionsAsChild(XDIAddress child, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult childDiscovery;

		// discovery

		try {

			childDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child);
			if (childDiscovery == null) throw new NullPointerException("Child not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for child " + child + ": " + ex.getMessage(), ex);
		}

		try {

			// message

			/*			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(childDiscovery.getCloudNumber().getXDIAddress());
			m.createGetOperation(XDIAddressUtil.concatXDIAddresses(
					childDiscovery.getCloudNumber().getXDIAddress(),
					XDI_ADD_CHAT_DO_EC));
			m.setToXDIAddress(childDiscovery.getCloudNumber().getXDIAddress());
			m.setLinkContractClass(RootLinkContract.class);
			m.setSecretToken(childSecretToken);

			XDIClient childClient = new XDIHttpClient(childDiscovery.getXdiEndpointUri());
			MessagingResponse mr = childClient.send(me);

			// result

			XdiEntityCollection linkContractXdiEntityCollection = XdiCommonRoot.findCommonRoot(mr.getGraph()).
					getXdiEntityCollection(
							XDIAddressUtil.concatXDIAddresses(
									childDiscovery.getCloudNumber().getXDIAddress(),
									XDI_ADD_CHAT_DO_EC),
							false);

			if (linkContractXdiEntityCollection == null) return new Connection[0];

			List<Connection> connections = new ArrayList<Connection> ();

			for (XdiEntityInstance xdiEntityInstance : linkContractXdiEntityCollection.getXdiInstancesUnordered()) {

				XdiEntity xdiEntity = xdiEntityInstance.dereference();
				GenericLinkContract linkContract = GenericLinkContract.fromXdiEntity(xdiEntity);

				if (linkContract == null) continue;

				connections.add(new XdiConnection(linkContract, null, mr.getGraph()));
			}*/

			// done

			//return connections.toArray(new Connection[connections.size()]);
			return null;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view connections as child " + child + ": " + ex.getMessage(), ex);
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

		try {

			// message

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

		try {

			// update chat link contract

			Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();

			GenericLinkContract linkContract1 = GenericLinkContract.findGenericLinkContract(
					tempGraph, 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					true);

			linkContract1.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, true).setLiteralBoolean(Boolean.TRUE);

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createSetOperation(tempGraph);
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), parentDiscovery.getCloudNumber().getXDIAddress()));
			//			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(linkContract1);
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

		try {

			// update chat link contract

			Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();

			GenericLinkContract linkContract1 = GenericLinkContract.findGenericLinkContract(
					tempGraph, 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					true);

			linkContract1.getXdiEntity().getXdiAttribute(XDI_ADD_BLOCKED, true).setLiteralBoolean(Boolean.FALSE);

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createSetOperation(tempGraph);
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), parentDiscovery.getCloudNumber().getXDIAddress()));
			//			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(linkContract1);
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

		try {

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createDelOperation(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), parentDiscovery.getCloudNumber().getXDIAddress()));

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress());
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot delete connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection findConnection(XDIAddress child1, XDIAddress child2, CloudNumber ascn, byte[] aspk, XDIAddress aslc) {

		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		GenericLinkContract linkContract1;
		GenericLinkContract linkContract2;

		// discovery

		try {

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		// get chat link contract of child1

		try {

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(child1Discovery.getCloudNumber().getXDIAddress());
			m.createGetOperation(chatLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress()));
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractClass(RootLinkContract.class);
			//			m.setSecretToken(child1SecretToken);

			// result

			XDIClient child1Client = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			MessagingResponse mr = child1Client.send(me);

			linkContract1 = GenericLinkContract.findGenericLinkContract(
					mr.getGraph(), 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					false);

			if (linkContract1 == null) return null;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot retrieve link contract of child1 " + child1 + ": " + ex.getMessage(), ex);
		}

		// get chat link contract of child2

		try {

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(child1Discovery.getCloudNumber().getXDIAddress());
			m.createGetOperation(chatLinkContractXDIAddress(child2Discovery.getCloudNumber().getXDIAddress(), child1Discovery.getCloudNumber().getXDIAddress())); 
			m.setToXDIAddress(child2Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(chatLinkContractXDIAddress(child2Discovery.getCloudNumber().getXDIAddress(), child1Discovery.getCloudNumber().getXDIAddress()));
			//			new RSAStaticPrivateKeySignatureCreator(child1PrivateKey).createSignature(m.getContextNode());

			// result

			XDIClient child2Client = new XDIHttpClient(child2Discovery.getXdiEndpointUri());
			MessagingResponse mr = child2Client.send(me);

			linkContract2 = GenericLinkContract.findGenericLinkContract(
					mr.getGraph(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					false);

			if (linkContract2 == null) return null;
		} catch (Exception ex) {

			return null;
		}

		// done

		return new XdiConnection(linkContract1, linkContract2);
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

		return GenericLinkContract.createGenericLinkContractXDIAddress(child1, child2, XDI_ADD_CHAT);
	}

	private static XDIAddress dependentLinkContractXDIAddress(XDIAddress child, XDIAddress parent) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child, parent, null);
	}
}
