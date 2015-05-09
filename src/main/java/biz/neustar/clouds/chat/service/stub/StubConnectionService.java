package biz.neustar.clouds.chat.service.stub;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.syntax.XDIAddress;
import biz.neustar.clouds.chat.exceptions.ConnectionNotFoundException;
import biz.neustar.clouds.chat.exceptions.NotParentOfChildException;
import biz.neustar.clouds.chat.model.Connection;
import biz.neustar.clouds.chat.model.Log;
import biz.neustar.clouds.chat.model.StubConnection;
import biz.neustar.clouds.chat.service.ConnectionService;
import biz.neustar.clouds.chat.service.ParentChildService;

public class StubConnectionService implements ConnectionService {

	private ParentChildService parentChildService;
	private List<StubConnection> connections;

	public StubConnectionService(ParentChildService parentChildService) {

		this.parentChildService = parentChildService;

		this.connections = new ArrayList<StubConnection> ();
	}

	@Override
	public StubConnection requestConnection(XDIAddress child1, XDIAddress child2) {

		StubConnection connection = new StubConnection(child1, child2);
		this.connections.add(connection);

		return connection;
	}

	@Override
	public StubConnection findConnection(XDIAddress child1, XDIAddress child2) {

		for (StubConnection connection : this.connections) {

			if (connection.getChild1().equals(child1) && connection.getChild2().equals(child2)) return connection;
			if (connection.getChild1().equals(child2) && connection.getChild2().equals(child1)) return connection;
		}

		return null;
	}

	@Override
	public StubConnection[] viewConnections(XDIAddress parentOrChild) {

		List<StubConnection> connections = new ArrayList<StubConnection> ();

		for (StubConnection connection : this.connections) {

			if (parentOrChild.equals(connection.getChild1()) ||
					parentOrChild.equals(connection.getChild2()) ||
					this.parentChildService.isParent(parentOrChild, connection.getChild1()) ||
					this.parentChildService.isParent(parentOrChild, connection.getChild2())) {

				connections.add(connection);
			}
		}

		return connections.toArray(new StubConnection[connections.size()]);
	}

	@Override
	public Log[] viewConnectionLogs(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		StubConnection connection = this.findConnection(child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = this.parentChildService.isParent(parent, child1);
		boolean isParent2 = this.parentChildService.isParent(parent, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		return connection.getLogs();
	}

	@Override
	public Connection approveConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		StubConnection connection = this.findConnection(child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = this.parentChildService.isParent(parent, child1);
		boolean isParent2 = this.parentChildService.isParent(parent, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		if (isParent1) connection.setApproved1(true);
		if (isParent2) connection.setApproved2(true);
		
		return connection;
	}

	@Override
	public Connection blockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		StubConnection connection = this.findConnection(child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = this.parentChildService.isParent(parent, child1);
		boolean isParent2 = this.parentChildService.isParent(parent, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		if (isParent1) connection.setBlocked1(true);
		if (isParent2) connection.setBlocked2(true);
		
		return connection;
	}

	@Override
	public Connection unblockConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		StubConnection connection = this.findConnection(child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = this.parentChildService.isParent(parent, child1);
		boolean isParent2 = this.parentChildService.isParent(parent, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		if (isParent1) connection.setBlocked1(false);
		if (isParent2) connection.setBlocked2(false);
		
		return connection;
	}

	@Override
	public Connection deleteConnection(XDIAddress parent, XDIAddress child1, XDIAddress child2) {

		StubConnection connection = this.findConnection(child1, child2);
		if (connection == null) throw new ConnectionNotFoundException("No connection found between " + child1 + " and " + child2);

		boolean isParent1 = this.parentChildService.isParent(parent, child1);
		boolean isParent2 = this.parentChildService.isParent(parent, child2);

		if (! isParent1 && ! isParent2) throw new NotParentOfChildException("" + parent + " is not a parent of either " + child1 + " or " + child2);

		this.connections.remove(connection);
		
		return connection;
	}
}
