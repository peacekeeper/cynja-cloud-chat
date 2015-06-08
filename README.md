<img src="http://neustarpc.github.com/neustar-clouds/images/logo.png"><br>

This is a simple chat server that integrates with XDI personal clouds.
It allows parents to manage chat connections of their children.

Sample deployment: http://52.7.194.93:3080/

### How to build

First, [XDI2](http://github.com/projectdanube/xdi2) needs to be build.

For now, use the snapshot-0.7-pre-no-value-node branch.

After that, just run

    mvn clean install

To build all components.

### How to run

	mvn jetty:run

Then go to:

	http://localhost:3080/1/

### Documentation

The API endpoints are:

	HTTP POST:  http://localhost:3080/1/request?child1=...&child1SecretToken=...&child2=...
	
	This is called by child1 to request a connection with child2.
	The connection has to be approved by parents of both children,
	before chat sessions can be started.
	
	HTTP POST:  http://localhost:3080/1/approve?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to approve the connection between child1 and child2.
	At least one parent of BOTH child1 and child2 has to approve the connection.
	
	HTTP POST:  http://localhost:3080/1/viewasparent?parent=...&parentSecretToken=...
	
	Returns the connections of this parent's  children.
	
	HTTP POST:  http://localhost:3080/1/viewaschild?child=...&childSecretToken=...
	
	Returns the connections of this child.
	
	HTTP POST:  http://localhost:3080/1/logs?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to view the recent chat messages for a given connection
	between child1 and child2.
	
	HTTP POST:  http://localhost:3080/1/block?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to "block" a connection between child1 and child2.
	This ends all currently open chat sessions between child1 and child2.
	The connection between child1 and child2 still exists, but no new chat session
	can be started, until the connection gets "unblocked" again.

	HTTP POST:  http://localhost:3080/1/unblock?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to "unblock" a connection between child1 and child2.
	After that, new chat sessions can be opened again.
	If the connection has been blocked by parents of both child1 and child2, then it
	also has to be unblocked again by both sides.

	HTTP POST:  http://localhost:3080/1/delete?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to "delete" a connection between child1 and child2.
	This ends all currently open chat sessions between child1 and child2.
	After that, a connection has to be re-created from start, using the "request" and
	"approve" API calls.

	WebSocket:  ws://localhost:3080/1/chat/{child1}/{child2}?child1SecretToken=...
	
	This is a WebSocket endpoint that provides actual chat sessions. The chat session
	has to be opened from both ends. For example, if child1 and child2 want to chat, they
	would open the following WebSocket URLs, respectively:
	ws://localhost:3080/1/chat/child1/child2?child1SecretToken=...
	ws://localhost:3080/1/chat/child2/child1?child2SecretToken=...
	During the WebSocket protocol handshake, the sub-protocol "cynja-chat" is used.

Notes:

	Make sure all path components and query values in the URLs are properly URL encoded.
