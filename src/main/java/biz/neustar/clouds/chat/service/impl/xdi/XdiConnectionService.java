package biz.neustar.clouds.chat.service.impl.xdi;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.InitFilter;
import biz.neustar.clouds.chat.exceptions.ConnectionNotFoundException;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.ConnectionService;
import xdi2.client.XDIClient;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.util.XDIClientUtil;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractInstanceUnordered;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityInstance;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.security.signature.create.RSAStaticPrivateKeySignatureCreator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public class XdiConnectionService implements ConnectionService {

	public static final XDIArc XDI_ARC_CHAT = XDIArc.create("#chat");
	public static final XDIAddress XDI_ADD_CHAT = XDIAddress.create("#chat");

	public static final XDIAddress XDI_ADD_CHAT_DO_EC = XDIAddress.create("#chat[$do]");

	public static final XDIAddress XDI_ADD_APPROVED = XDIAddress.create("<#approved>");
	public static final XDIAddress XDI_ADD_BLOCKED = XDIAddress.create("<#blocked>");

	@Override
	public Connection requestConnection(XDIAddress child1, String child1SecretToken, XDIAddress child2) {

		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;

		// discovery

		try {

			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");

			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		try {

			// create chat link contract 1

			Graph tempGraph1 = MemoryGraphFactory.getInstance().openGraph();

			GenericLinkContract linkContract1 = GenericLinkContract.findGenericLinkContract(
					tempGraph1, 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					true);

			linkContract1.setPermissionTargetXDIAddress(
					XDILinkContractConstants.XDI_ADD_GET, 
					linkContract1.getXdiEntity().getXDIAddress());

			// create a $ref equivalence link from a #chat[$do] collection member to the chat link contract
			// this way, it becomes possible later to easily list all chat link contracts

			XdiEntityCollection linkContract1XdiEntityCollection = XdiCommonRoot.findCommonRoot(tempGraph1).
					getXdiEntityCollection(
							XDIAddressUtil.concatXDIAddresses(
									child1Discovery.getCloudNumber().getXDIAddress(),
									XDI_ADD_CHAT_DO_EC),
									true);

			XdiEntityInstanceUnordered linkContract1XdiEntityInstance = linkContract1XdiEntityCollection
					.setXdiInstanceUnordered(XDIArc.literalFromDigest(
							linkContract1.getContextNode().getXDIAddress().toString()));

			Equivalence.setReferenceContextNode(linkContract1XdiEntityInstance.getContextNode(), linkContract1.getContextNode());

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(child1Discovery.getCloudNumber().getXDIAddress());
			m.createSetOperation(tempGraph1);
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractClass(RootLinkContract.class);
			m.setSecretToken(child1SecretToken);

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// create chat link contract 2

			Graph tempGraph2 = MemoryGraphFactory.getInstance().openGraph();

			GenericLinkContract linkContract2 = GenericLinkContract.findGenericLinkContract(
					tempGraph2, 
					child2Discovery.getCloudNumber().getXDIAddress(), 
					child1Discovery.getCloudNumber().getXDIAddress(), 
					XDI_ADD_CHAT, 
					null,
					true);

			linkContract2.setPermissionTargetXDIAddress(
					XDILinkContractConstants.XDI_ADD_GET, 
					linkContract2.getXdiEntity().getXDIAddress());

			// done

			Connection connection = new XdiConnection(linkContract1);

			return connection;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot request connection as child1 " + child1 + " to child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection[] viewConnectionsAsParent(XDIAddress parent, String parentSecretToken) {

		XDIDiscoveryResult parentDiscovery;
		PrivateKey parentPrivateKey;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");

			parentPrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(parentDiscovery.getCloudNumber(), parentDiscovery.getXdiEndpointUri(), parentSecretToken);
			if (parentPrivateKey == null) throw new NullPointerException("Parent parent key not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + ": " + ex.getMessage(), ex);
		}

		try {

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

					connections.add(new XdiConnection(linkContract));
				}
			}

			// done

			return connections.toArray(new Connection[connections.size()]);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view connections as parent " + parent + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection[] viewConnectionsAsChild(XDIAddress child, String childSecretToken) {

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

			MessageEnvelope me = new MessageEnvelope();
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

				connections.add(new XdiConnection(linkContract));
			}

			// done

			return connections.toArray(new Connection[connections.size()]);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view connections as child " + child + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Log[] logsConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;
		PrivateKey parentPrivateKey;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
			
			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");
			
			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");

			parentPrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(parentDiscovery.getCloudNumber(), parentDiscovery.getXdiEndpointUri(), parentSecretToken);
			if (parentPrivateKey == null) throw new NullPointerException("Parent private key not found.");
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot find discovery information for parent " + parent + " or child1 " + child1 + " or child2 " + child2 + ": " + ex.getMessage(), ex);
		}

		try {

			// message

			MessageEnvelope me = new MessageEnvelope();
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

			// done

			Connection connection = new XdiConnection(linkContract1);

			return CynjaCloudChat.logService.getLogs(connection);
		} catch (ConnectionNotFoundException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot view logs of connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection approveConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;
		PrivateKey parentPrivateKey;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
			
			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");
			
			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");

			parentPrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(parentDiscovery.getCloudNumber(), parentDiscovery.getXdiEndpointUri(), parentSecretToken);
			if (parentPrivateKey == null) throw new NullPointerException("Parent private key not found.");
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

			linkContract1.setPermissionTargetXDIAddress(
					XDILinkContractConstants.XDI_ADD_GET, 
					linkContract1.getXdiEntity().getXDIAddress());

			linkContract1.getXdiEntity().getXdiAttribute(XDI_ADD_APPROVED, true).setLiteralBoolean(Boolean.TRUE);

			// create a $ref equivalence link from a #chat[$do] collection member to the chat link contract
			// this way, it becomes possible later to easily list all chat link contracts

			XdiEntityCollection linkContract1XdiEntityCollection = XdiCommonRoot.findCommonRoot(tempGraph).
					getXdiEntityCollection(
							XDIAddressUtil.concatXDIAddresses(
									child1Discovery.getCloudNumber().getXDIAddress(),
									XDI_ADD_CHAT_DO_EC),
									true);

			XdiEntityInstanceUnordered linkContract1XdiEntityInstance = linkContract1XdiEntityCollection
					.setXdiInstanceUnordered(XDIArc.literalFromDigest(
							linkContract1.getContextNode().getXDIAddress().toString()));

			Equivalence.setReferenceContextNode(linkContract1XdiEntityInstance.getContextNode(), linkContract1.getContextNode());

			// message

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(parentDiscovery.getCloudNumber().getXDIAddress());
			m.createSetOperation(tempGraph);
			m.setToXDIAddress(child1Discovery.getCloudNumber().getXDIAddress());
			m.setLinkContractXDIAddress(dependentLinkContractXDIAddress(child1Discovery.getCloudNumber().getXDIAddress(), parentDiscovery.getCloudNumber().getXDIAddress()));
			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(linkContract1);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot approve connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection blockConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;
		PrivateKey parentPrivateKey;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
			
			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");
			
			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");

			parentPrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(parentDiscovery.getCloudNumber(), parentDiscovery.getXdiEndpointUri(), parentSecretToken);
			if (parentPrivateKey == null) throw new NullPointerException("Parent private key not found.");
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
			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(linkContract1);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot block connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection unblockConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;
		PrivateKey parentPrivateKey;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
			
			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");
			
			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");

			parentPrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(parentDiscovery.getCloudNumber(), parentDiscovery.getXdiEndpointUri(), parentSecretToken);
			if (parentPrivateKey == null) throw new NullPointerException("Parent private key not found.");
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
			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(linkContract1);
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot unblock connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection deleteConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		XDIDiscoveryResult parentDiscovery;
		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;
		PrivateKey parentPrivateKey;

		// discovery

		try {

			parentDiscovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(parent);
			if (parentDiscovery == null) throw new NullPointerException("Parent not found.");
			
			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");
			
			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");

			parentPrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(parentDiscovery.getCloudNumber(), parentDiscovery.getXdiEndpointUri(), parentSecretToken);
			if (parentPrivateKey == null) throw new NullPointerException("Parent private key not found.");
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
			new RSAStaticPrivateKeySignatureCreator(parentPrivateKey).createSignature(m.getContextNode());

			XDIClient childClient = new XDIHttpClient(child1Discovery.getXdiEndpointUri());
			childClient.send(me);

			// done

			return new XdiConnection(child1Discovery.getCloudNumber().getXDIAddress(), child2Discovery.getCloudNumber().getXDIAddress());
		} catch (Exception ex) {

			throw new ConnectionNotFoundException("Cannot delete connection as parent " + parent + " with child1 " + child1 + " an child2 " + child2 + ": " + ex.getMessage(), ex);
		}
	}

	@Override
	public Connection findConnection(XDIAddress child1, String child1SecretToken, XDIAddress child2) {

		XDIDiscoveryResult child1Discovery;
		XDIDiscoveryResult child2Discovery;
		PrivateKey child1PrivateKey;

		GenericLinkContract linkContract1;
		GenericLinkContract linkContract2;

		// discovery

		try {
			
			child1Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child1);
			if (child1Discovery == null) throw new NullPointerException("Child 1 not found.");
			
			child2Discovery = InitFilter.XDI_DISCOVERY_CLIENT.discoverFromRegistry(child2);
			if (child2Discovery == null) throw new NullPointerException("Child 2 not found.");

			child1PrivateKey = XDIClientUtil.retrieveSignaturePrivateKey(child1Discovery.getCloudNumber(), child1Discovery.getXdiEndpointUri(), child1SecretToken);
			if (child1PrivateKey == null) throw new NullPointerException("Child 1 private key not found.");
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
			m.setSecretToken(child1SecretToken);

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
			new RSAStaticPrivateKeySignatureCreator(child1PrivateKey).createSignature(m.getContextNode());

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

	private static XDIAddress chatLinkContractXDIAddress(XDIAddress child1, XDIAddress child2) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child1, child2, XDI_ADD_CHAT);
	}

	private static XDIAddress dependentLinkContractXDIAddress(XDIAddress child, XDIAddress parent) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(child, parent, null);
	}
}
