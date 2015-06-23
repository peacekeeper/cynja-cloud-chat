<img src="http://neustarpc.github.com/neustar-clouds/images/logo.png"><br>

This is a simple chat server that integrates with XDI personal clouds.
It allows parents to manage chat connections of their children.

Sample deployment: http://chatapi.respectnetwork.net:3080/

### How to build

First, [XDI2](http://github.com/projectdanube/xdi2) needs to be build.

For now, use the snapshot-0.7-pre-no-value-node branch.

After that, just run

    mvn clean install

To build all components.

### How to run

	mvn jetty:run

Then go to:

	http://localhost:3080/

### Documentation

#### API

	HTTP POST:  http://localhost:3080/1/request?child1=...&child1SecretToken=...&child2=...
	
	This is called by child1 to request a connection with child2.
	The connection has to be approved by parents of both children,
	before chat sessions can be started.
	
	Example response:
	
	(none)
	
---
	
	HTTP POST:  http://localhost:3080/1/approve?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to approve the connection between child1 and child2.
	At least one parent of BOTH child1 and child2 has to approve the connection.
	
	Example response:
	
	(none)
	
---
	
	HTTP POST:  http://localhost:3080/1/viewasparent?parent=...&parentSecretToken=...
	
	Returns the connections of this parent's  children.
	
	Example response:
	
	{
	    "[=]!:uuid:06dbdc63-ec66-4fab-81d9-c7c5862716c1": [
	        {
	            "child": "[=]!:uuid:37799d36-b79f-4e60-a42c-7a95ea407e24",
	            "approved": true,
	            "blocked": null,
	            "sessions": []
	        },
	        {
	            "child": "[=]!:uuid:a1453ce6-b176-4407-b4cd-ceff48954eb5",
	            "approved": null,
	            "blocked": null,
	            "sessions": []
	        },
	        {
	            "child": "[=]!:uuid:fd6b3063-847b-43be-9ba8-c1a910b22754",
	            "approved": true,
	            "blocked": false,
	            "sessions": []
	        }
	    ],
	    "[=]!:uuid:a827df5b-f0c7-45d0-80d6-ceb2876e789a": [
	        {
	            "child": "[=]!:uuid:a1453ce6-b176-4407-b4cd-ceff48954eb5",
	            "approved": true,
	            "blocked": null,
	            "sessions": []
	        }
	    ]
	}	

---
	
	HTTP POST:  http://localhost:3080/1/viewaschild?child=...&childSecretToken=...
	
	Returns the connections of this child.
	
	Example response:
	
	{
	    "[=]!:uuid:06dbdc63-ec66-4fab-81d9-c7c5862716c1": [
	        {
	            "child": "[=]!:uuid:37799d36-b79f-4e60-a42c-7a95ea407e24",
	            "approved": true,
	            "blocked": null,
	            "sessions": []
	        },
	        {
	            "child": "[=]!:uuid:a1453ce6-b176-4407-b4cd-ceff48954eb5",
	            "approved": null,
	            "blocked": null,
	            "sessions": []
	        },
	        {
	            "child": "[=]!:uuid:fd6b3063-847b-43be-9ba8-c1a910b22754",
	            "approved": true,
	            "blocked": false,
	            "sessions": []
	        }
	    ]
	}	
	
---
	
	HTTP POST:  http://localhost:3080/1/logs?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to view the recent chat messages for a given connection
	between child1 and child2.
	
	Example response:
	
	[
	    {
	        "chatChild1": "=sandeep-chd1",
	        "chatChild2": "=ritesh-chd1",
	        "connectionChild1": "[=]!:uuid:fd6b3063-847b-43be-9ba8-c1a910b22754",
	        "connectionChild2": "[=]!:uuid:06dbdc63-ec66-4fab-81d9-c7c5862716c1",
	        "message": "hi",
	        "date": "Tuesday, June 23, 2015 2:10:00 PM UTC"
	    },
	    {
	        "chatChild1": "=ritesh-chd1",
	        "chatChild2": "=sandeep-chd1",
	        "connectionChild1": "[=]!:uuid:06dbdc63-ec66-4fab-81d9-c7c5862716c1",
	        "connectionChild2": "[=]!:uuid:fd6b3063-847b-43be-9ba8-c1a910b22754",
	        "message": "hello",
	        "date": "Tuesday, June 23, 2015 2:10:04 PM UTC"
	    },
	    {
	        "chatChild1": "=sandeep-chd1",
	        "chatChild2": "=ritesh-chd1",
	        "connectionChild1": "[=]!:uuid:fd6b3063-847b-43be-9ba8-c1a910b22754",
	        "connectionChild2": "[=]!:uuid:06dbdc63-ec66-4fab-81d9-c7c5862716c1",
	        "message": "how are you",
	        "date": "Tuesday, June 23, 2015 2:10:09 PM UTC"
	    }
	]
	
---
	
	HTTP POST:  http://localhost:3080/1/block?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to "block" a connection between child1 and child2.
	This ends all currently open chat sessions between child1 and child2.
	The connection between child1 and child2 still exists, but no new chat session
	can be started, until the connection gets "unblocked" again.
	
	Example response:
	
	(none)
	
---

	HTTP POST:  http://localhost:3080/1/unblock?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to "unblock" a connection between child1 and child2.
	After that, new chat sessions can be opened again.
	If the connection has been blocked by parents of both child1 and child2, then it
	also has to be unblocked again by both sides.
	
	Example response:
	
	(none)
	
---

	HTTP POST:  http://localhost:3080/1/delete?parent=...&parentSecretToken=...&child1=...&child2=...
	
	This is called by a parent to "delete" a connection between child1 and child2.
	This ends all currently open chat sessions between child1 and child2.
	After that, a connection has to be re-created from start, using the "request" and
	"approve" API calls.
	
	Example response:
	
	(none)
	
---

	WebSocket:  ws://localhost:3080/1/chat/{child1}/{child2}?child1SecretToken=...
	
	This is a WebSocket endpoint that provides actual chat sessions. The chat session
	has to be opened from both ends. For example, if child1 and child2 want to chat, they
	would open the following WebSocket URLs, respectively:
	ws://localhost:3080/1/chat/child1/child2?child1SecretToken=...
	ws://localhost:3080/1/chat/child2/child1?child2SecretToken=...
	During the WebSocket protocol handshake, the sub-protocol "cynja-chat" is used.

---

#### Parameters:

	child1 ... Child 1's cloud name or cloud number
	child2 ... Child 2's cloud name or cloud number
	parent ... The parent's cloud name or cloud number
	child1SecretToken ... Child 1's secret token (password)
	parentSecretToken ... The parent's secret token (password)

---

#### Notes:

	Make sure all path components and query values in the URLs are properly URL encoded.
