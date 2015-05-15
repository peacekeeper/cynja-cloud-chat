<html>

<head>

<title>Cynja Cloud Chat</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

<link rel="stylesheet" href="css/style.css" TYPE="text/css" MEDIA="screen" />

<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/lodash.compat.min.js"></script>

<script type="text/javascript">

function request() {

	var child1 = $("#requestChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child1SecretToken = $("#requestChild1SecretToken").val().trim(); if (! child1SecretToken) { alert("Please enter \"Child 1 Secret Token\""); return; }
	var child2 = $("#requestChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }

	$.ajax({
	    url: '/1/request',
	    type: 'POST',
	    data: 'child1=' + encodeURIComponent(child1) + '&' + 'child1SecretToken=' + encodeURIComponent(child1SecretToken) + '&' + 'child2=' + encodeURIComponent(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function approve() {

	var parent = $("#approveParent").val().trim(); if (! parent) { alert("Please enter \"Parent\""); return; }
	var parentSecretToken = $("#approveParentSecretToken").val().trim(); if (! parentSecretToken) { alert("Please enter \"Parent Secret Token\""); return; }
	var child1 = $("#approveChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child2 = $("#approveChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }

	$.ajax({
	    url: '/1/approve',
	    type: 'POST',
	    data: 'parent=' + encodeURIComponent(parent) + '&' + 'parentSecretToken=' + encodeURIComponent(parentSecretToken) + '&' + 'child1=' + encodeURIComponent(child1) + '&' + 'child2=' + encodeURIComponent(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function viewasparent() {

	var parent = $("#viewasparentParent").val().trim(); if (! parent) { alert("Please enter \"Parent\""); return; }
	var parentSecretToken = $("#viewasparentParentSecretToken").val().trim(); if (! parentSecretToken) { alert("Please enter \"Parent Secret Token\""); return; }

	$.ajax({
	    url: '/1/viewasparent',
	    type: 'POST',
	    data: 'parent=' + encodeURIComponent(parent) + '&' + 'parentSecretToken=' + encodeURIComponent(parentSecretToken),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function viewaschild() {

	var child = $("#viewaschildChild").val().trim(); if (! child) { alert("Please enter \"Child\""); return; }
	var childSecretToken = $("#viewaschildChildSecretToken").val().trim(); if (! childSecretToken) { alert("Please enter \"Child Secret Token\""); return; }

	$.ajax({
	    url: '/1/viewaschild',
	    type: 'POST',
	    data: 'child=' + encodeURIComponent(child) + '&' + 'childSecretToken=' + encodeURIComponent(childSecretToken),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function logs() {

	var parent = $("#logsParent").val().trim(); if (! parent) { alert("Please enter \"Parent\""); return; }
	var parentSecretToken = $("#logsParentSecretToken").val().trim(); if (! parentSecretToken) { alert("Please enter \"Parent Secret Token\""); return; }
	var child1 = $("#logsChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child2 = $("#logsChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }

	$.ajax({
	    url: '/1/logs',
	    type: 'POST',
	    data: 'parent=' + encodeURIComponent(parent) + '&' + 'parentSecretToken=' + encodeURIComponent(parentSecretToken) + '&' + 'child1=' + encodeURIComponent(child1) + '&' + 'child2=' + encodeURIComponent(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function block() {

	var parent = $("#blockParent").val().trim(); if (! parent) { alert("Please enter \"Parent\""); return; }
	var parentSecretToken = $("#blockParentSecretToken").val().trim(); if (! parentSecretToken) { alert("Please enter \"Parent Secret Token\""); return; }
	var child1 = $("#blockChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child2 = $("#blockChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }

	$.ajax({
	    url: '/1/block',
	    type: 'POST',
	    data: 'parent=' + encodeURIComponent(parent) + '&' + 'parentSecretToken=' + encodeURIComponent(parentSecretToken) + '&' + 'child1=' + encodeURIComponent(child1) + '&' + 'child2=' + encodeURIComponent(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function unblock() {

	var parent = $("#unblockParent").val().trim(); if (! parent) { alert("Please enter \"Parent\""); return; }
	var parentSecretToken = $("#unblockParentSecretToken").val().trim(); if (! parentSecretToken) { alert("Please enter \"Parent Secret Token\""); return; }
	var child1 = $("#unblockChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child2 = $("#unblockChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }

	$.ajax({
	    url: '/1/unblock',
	    type: 'POST',
	    data: 'parent=' + encodeURIComponent(parent) + '&' + 'parentSecretToken=' + encodeURIComponent(parentSecretToken) + '&' + 'child1=' + encodeURIComponent(child1) + '&' + 'child2=' + encodeURIComponent(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function delet() {

	var parent = $("#deleteParent").val().trim(); if (! parent) { alert("Please enter \"Parent\""); return; }
	var parentSecretToken = $("#deleteParentSecretToken").val().trim(); if (! parentSecretToken) { alert("Please enter \"Parent Secret Token\""); return; }
	var child1 = $("#deleteChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child2 = $("#deleteChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }

	$.ajax({
	    url: '/1/delete',
	    type: 'POST',
	    data: 'parent=' + encodeURIComponent(parent) + '&' + 'parentSecretToken=' + encodeURIComponent(parentSecretToken) + '&' + 'child1=' + encodeURIComponent(child1) + '&' + 'child2=' + encodeURIComponent(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

var ws = null;

function chatStart() {

	if (ws) chatStop();

	var child1 = $("#chatChild1").val().trim(); if (! child1) { alert("Please enter \"Child 1\""); return; }
	var child2 = $("#chatChild2").val().trim(); if (! child2) { alert("Please enter \"Child 2\""); return; }
	var child1SecretToken = $("#chatChild1SecretToken").val().trim(); if (! child1SecretToken) { alert("Please enter \"Child 1 Secret Token\""); return; }

	var url = window.location.href.replace("http", "ws") + "1/chat/" + encodeURIComponent(child1) + '/' + encodeURIComponent(child2) + '?child1SecretToken=' + child1SecretToken;

	ws = new WebSocket(url, ["cynja-chat"]);

	ws.onmessage = function(event) {

		$('#messages').val($('#messages').val() + event.data + "\n");
	};

	ws.onerror = function(event) {

		alert('Chat error: ' + event.data);
	};

	ws.onopen = function(event) {

		alert('Chat opened.');
		$('#messages').val('');
	};

	ws.onclose = function(event) {

		alert('Chat closed: ' + event.code + ' ' + event.reason);
		$('#messages').val('');
	};
}

function chatStop() {

	if (! ws) { alert('No open chat.'); return; }

	ws.close();
	ws = null;
}

function chatMessage() {

	if (! ws) { alert('No open chat.'); return; }

	var chatMessage = $("#chatMessage").val().trim();

	ws.send(chatMessage);
	$("chatMessage").val('');
}

</script>

</head>

<body>

<!-- examples for stub implementation

<pre id="example">
 * CYNJA CLOUD CHAT - example parent/child data - <a href="https://github.com/neustarpc/cynja-cloud-chat">https://github.com/neustarpc/cynja-cloud-chat</a>
 * ================

 * Parents                                                      * Parents
 *   [=]!:uuid:1111 and [=]!:uuid:2222                          *   [=]!:uuid:5555 and [=]!:uuid:6666
 * have the following children                                  * have the following children
 *   [=]!:uuid:3333, [=]!:uuid:4444                             *   [=]!:uuid:7777, [=]!:uuid:8888, [=]!:uuid:9999
 
 * Secret token for all parents and children: abcd
</pre>

-->

<pre id="example">
 * CYNJA CLOUD CHAT - example parent/child data - <a href="https://github.com/neustarpc/cynja-cloud-chat">https://github.com/neustarpc/cynja-cloud-chat</a>
 * ================

 * Parents                                                            * Parents
 *   =cynja1 / [=]!:uuid:24909bdb-8f22-4abe-a244-b042adb32b5d         *   =cynja2 / [=]!:uuid:090fba09-cb57-4822-a1c7-b7987e7d62e5
 * have the following children                                        * have the following children
 *   =cynja1-dep1 / [=]!:uuid:3d80d15d-b22b-4ebd-8f80-dd1fa7fdb858    *   =cynja2-dep1 / [=]!:uuid:960162ce-5fed-481b-878f-f3b4da86a31b
 *   =cynja1-dep2 / [=]!:uuid:1a8c8b52-eeb2-403c-8211-8f2924afff1c    *   =cynja2-dep1 / [=]!:uuid:49a806d8-529a-43ca-96ec-4656e8c7f907
 
 * Secret token for all parents and children: test@123
</pre>

<div id="chat">
<table>
<tr><td>Child 1:</td><td><input type="text" id="chatChild1"></td></tr>
<tr><td>Child 2:</td><td><input type="text" id="chatChild2"></td></tr>
<tr><td>Child 1 Secret Token:</td><td><input type="text" id="chatChild1SecretToken"></td></tr>
<tr><td><button onclick="chatStart();">Start Chat</button></td><td><button onclick="chatStop();">Stop Chat</button></td></tr>
</table>
<textarea id="messages"></textarea>
<table>
<tr>
<td>Chat Message:</td>
<td><input type="text" id="chatMessage"></td>
<td><button onclick="chatMessage();">Send Message</button></td>
</tr>
</table>
</div>

<div>
<p class="heading">Request Connection</p>
<table>
<tr><td>Child 1</td><td><input type="text" id="requestChild1"></td></tr>
<tr><td>Child 1 Secret Token</td><td><input type="text" id="requestChild1SecretToken"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="requestChild2"></td></tr>
</table>
<button onclick="request();">Request</button>
</div>

<div>
<p class="heading">Approve Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="approveParent"></td></tr>
<tr><td>Parent Secret Token</td><td><input type="text" id="approveParentSecretToken"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="approveChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="approveChild2"></td></tr>
</table>
<button onclick="approve();">Approve</button>
</div>

<div>
<p class="heading">View Connections As Parent</p>
<table>
<tr><td>Parent</td><td><input type="text" id="viewasparentParent"></td></tr>
<tr><td>Parent Secret Token</td><td><input type="text" id="viewasparentParentSecretToken"></td></tr>
</table>
<button onclick="viewasparent();">View As Parent</button>
</div>

<div>
<p class="heading">View Connections As Child</p>
<table>
<tr><td>Child</td><td><input type="text" id="viewaschildChild"></td></tr>
<tr><td>Child Secret Token</td><td><input type="text" id="viewaschildChildSecretToken"></td></tr>
</table>
<button onclick="viewaschild();">View As Child</button>
</div>

<div>
<p class="heading">View Connection Log</p>
<table>
<tr><td>Parent</td><td><input type="text" id="logsParent"></td></tr>
<tr><td>Parent Secret Token</td><td><input type="text" id="logsParentSecretToken"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="logsChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="logsChild2"></td></tr>
</table>
<button onclick="log();">View Log</button>
</div>

<div>
<p class="heading">Block Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="blockParent"></td></tr>
<tr><td>Parent Secret Token</td><td><input type="text" id="blockParentSecretToken"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="blockChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="blockChild2"></td></tr>
</table>
<button onclick="block();">Block</button>
</div>

<div>
<p class="heading">Unblock Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="unblockParent"></td></tr>
<tr><td>Parent Secret Token</td><td><input type="text" id="unblockParentSecretToken"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="unblockChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="unblockChild2"></td></tr>
</table>
<button onclick="unblock();">Unblock</button>
</div>

<div>
<p class="heading">Delete Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="deleteParent"></td></tr>
<tr><td>Parent Secret Token</td><td><input type="text" id="deleteParentSecretToken"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="deleteChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="deleteChild2"></td></tr>
</table>
<button onclick="delet();">Delete</button>
</div>

</body>

</html>
