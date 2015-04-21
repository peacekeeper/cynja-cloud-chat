<img src="http://neustarpc.github.com/neustar-clouds/images/logo.png"><br>

This is a simple chat server that integrates with XDI personal clouds.
It allows parents to manage chat connections of their children.

### How to build

First, [XDI2](http://github.com/projectdanube/xdi2) needs to be build.

After that, just run

    mvn clean install

To build all components.

### How to run

	mvn jetty:run

Then go to:

	http://localhost:3080/

### Documentation

The API endpoints are:

	HTTP POST:  http://localhost:3080/establish?child1=...&child2=...
	This is called by child1 to establish a connection with child2.
	The connection has to be approved by parents of both children in order to work.
	
	HTTP POST:  http://localhost:3080/approve?parent=...&child1=...&child2=...
	This is called by a parent to approve the connection between child1 and child2.
	At least one parent of BOTH child1 and child2 has to approve the connection.
	
	HTTP POST:  http://localhost:3080/view?parentOrChild=...
	If this is called by a child, it returns his/her connections.
	If this is called by a parent, it returns the connections of his/her children.
	
	HTTP POST:  http://localhost:3080/log?parent=...&child1=...&child2=...
	This is called by a parent to view the recent chat messages for a given connection between child1 and child 2.
	
	HTTP POST:  http://localhost:3080/block?parent=...&child1=...&child2=...
	This is called by a parent to "block" a connection between child1 and child2.
	This ends any existing chat session that may exist right now between child1 and child2.
	The connection between child1 and child2 still exists, but no chat session can
	be started, until the connection gets "unblocked" again.

	HTTP POST:  http://localhost:3080/unblock?parent=...&child1=...&child2=...
	This is called by a parent to "unblock" a connection between child1 and child2.
	After that, new chat session can be opened again.
	If the connection has been blocked by parents of both child1 and child2, then it
	also has to be unblocked by both sides again.

	HTTP POST:  http://localhost:3080/delete?parent=...&child1=...&child2=...
	This is called by a parent to "delete" a connection between child1 and child2.
	This ends any existing chat session that may exist right now between child1 and child2.
	After that, a connection has to be re-created from start, using the "establish" and
	"approve" API calls.

	WebSocket:  ws://localhost:3080/chat/{fromchild}/{tochild}
	This is a WebSocket endpoint that provides actual chat sessions. The chat session
	has to be opened from both ends. For example, if child1 and child2 want to chat, they
	would open the following WebSocket URLs, respectively:
	ws://localhost:3080/chat/child1/child2
	ws://localhost:3080/chat/child2/child1
	During the WebSocket protocol handshake, the sub-protocol "cynja-chat" is used.
