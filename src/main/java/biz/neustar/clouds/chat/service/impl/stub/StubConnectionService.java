package biz.neustar.clouds.chat.service.impl.stub;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.CynjaCloudChat;
import biz.neustar.clouds.chat.exceptions.ConnectionNotFoundException;
import biz.neustar.clouds.chat.exceptions.NotParentOfChildException;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.service.ConnectionService;

public class StubConnectionService implements ConnectionService {

	private static final String STUB_SECRET_TOKEN = "abcd";

	private List<StubConnection> connections;

	public StubConnectionService() {

		this.connections = new ArrayList<StubConnection> ();
	}

	/*
	 * Implementation
	 */

	@Override
	public Connection requestConnection(XDIAddress child1, String child1SecretToken, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(child1SecretToken)) throw new RuntimeException("Invalid child1 secret token for " + child1);

		StubConnection connection = new StubConnection(child1, child2);
		this.connections.add(connection);

		return connection;
	}

	@Override
	public Connection approveConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		StubConnection connection = this.findConnection(parent, parentSecretToken, child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child1);
		boolean isParent2 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		if (isParent1) connection.setApproved1(true);
		if (isParent2) connection.setApproved2(true);

		return connection;
	}

	@Override
	public StubConnection[] viewConnectionsAsParent(XDIAddress parent, String parentSecretToken) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		XDIAddress[] children = CynjaCloudChat.parentChildService.getChildren(parent, parentSecretToken);

		List<StubConnection> connections = new ArrayList<StubConnection> ();

		for (XDIAddress child : children) {

			for (StubConnection connection : this.connections) {

				if (child.equals(connection.getChild1()) ||
						child.equals(connection.getChild2())) {

					connections.add(connection);
				}
			}
		}

		return connections.toArray(new StubConnection[connections.size()]);
	}

	@Override
	public StubConnection[] viewConnectionsAsChild(XDIAddress child, String childSecretToken) {

		if (! STUB_SECRET_TOKEN.equals(childSecretToken)) throw new RuntimeException("Invalid child secret token for " + child);

		List<StubConnection> connections = new ArrayList<StubConnection> ();

		for (StubConnection connection : this.connections) {

			if (child.equals(connection.getChild1()) ||
					child.equals(connection.getChild2())) {

				connections.add(connection);
			}
		}

		return connections.toArray(new StubConnection[connections.size()]);
	}

	@Override
	public Log[] logsConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		StubConnection connection = this.findConnection(parent, parentSecretToken, child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child1);
		boolean isParent2 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		return CynjaCloudChat.logService.getLogs(connection);
	}

	@Override
	public Connection blockConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		StubConnection connection = this.findConnection(parent, parentSecretToken, child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child1);
		boolean isParent2 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		if (isParent1) connection.setBlocked1(true);
		if (isParent2) connection.setBlocked2(true);

		return connection;
	}

	@Override
	public Connection unblockConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		StubConnection connection = this.findConnection(parent, parentSecretToken, child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child1);
		boolean isParent2 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		if (isParent1) connection.setBlocked1(false);
		if (isParent2) connection.setBlocked2(false);

		return connection;
	}

	@Override
	public Connection deleteConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		StubConnection connection = this.findConnection(parent, parentSecretToken, child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child1);
		boolean isParent2 = CynjaCloudChat.parentChildService.isParent(parent, parentSecretToken, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		this.connections.remove(connection);

		return connection;
	}

	@Override
	public StubConnection findConnection(XDIAddress child1, String child1SecretToken, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(child1SecretToken)) throw new RuntimeException("Invalid child1 secret token for " + child1);

		for (StubConnection connection : this.connections) {

			if (connection.getChild1().equals(child1) && connection.getChild2().equals(child2)) return connection;
			if (connection.getChild1().equals(child2) && connection.getChild2().equals(child1)) return connection;
		}

		return null;
	}

	/*
	 * Helper methods
	 */

	public StubConnection findConnection(XDIAddress parent, String parentSecretToken, XDIAddress child1, XDIAddress child2) {

		if (! STUB_SECRET_TOKEN.equals(parentSecretToken)) throw new RuntimeException("Invalid parent secret token for " + parent);

		for (StubConnection connection : this.connections) {

			if (connection.getChild1().equals(child1) && connection.getChild2().equals(child2)) return connection;
			if (connection.getChild1().equals(child2) && connection.getChild2().equals(child1)) return connection;
		}

		return null;
	}
}
