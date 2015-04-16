<html>

<head>

<title>Cynja Cloud Chat</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

<link rel="stylesheet" href="css/style.css" TYPE="text/css" MEDIA="screen" />

<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/lodash.compat.min.js"></script>

<script type="text/javascript">

function establish() {

	var child1 = $("#establishChild1").val();
	var child2 = $("#establishChild2").val();

	$.ajax({
	    url: '/establish',
	    type: 'POST',
	    data: 'child1=' + _.escape(child1) + '&' + 'child2=' + _.escape(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function approve() {

	var parent = $("#approveParent").val();
	var child1 = $("#approveChild1").val();
	var child2 = $("#approveChild2").val();

	$.ajax({
	    url: '/approve',
	    type: 'POST',
	    data: 'parent=' + _.escape(parent) + '&' + 'child1=' + _.escape(child1) + '&' + 'child2=' + _.escape(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function view() {

	var parentOrChild = $("#viewParentOrChild").val();

	$.ajax({
	    url: '/view',
	    type: 'POST',
	    data: 'parentOrChild=' + _.escape(parentOrChild),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function log() {

	var parent = $("#logParent").val();
	var child1 = $("#logChild1").val();
	var child2 = $("#logChild2").val();

	$.ajax({
	    url: '/log',
	    type: 'POST',
	    data: 'parent=' + _.escape(parent) + '&' + 'child1=' + _.escape(child1) + '&' + 'child2=' + _.escape(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function block() {

	var parent = $("#blockParent").val();
	var child1 = $("#blockChild1").val();
	var child2 = $("#blockChild2").val();

	$.ajax({
	    url: '/block',
	    type: 'POST',
	    data: 'parent=' + _.escape(parent) + '&' + 'child1=' + _.escape(child1) + '&' + 'child2=' + _.escape(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function unblock() {

	var parent = $("#unblockParent").val();
	var child1 = $("#unblockChild1").val();
	var child2 = $("#unblockChild2").val();

	$.ajax({
	    url: '/unblock',
	    type: 'POST',
	    data: 'parent=' + _.escape(parent) + '&' + 'child1=' + _.escape(child1) + '&' + 'child2=' + _.escape(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function delet() {

	var parent = $("#deleteParent").val();
	var child1 = $("#deleteChild1").val();
	var child2 = $("#deleteChild2").val();

	$.ajax({
	    url: '/delete',
	    type: 'POST',
	    data: 'parent=' + _.escape(parent) + '&' + 'child1=' + _.escape(child1) + '&' + 'child2=' + _.escape(child2),
	    success: function(data) { alert('success: ' + JSON.stringify(data)); },
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

var ws = null;

function chatStart() {

	if (ws) chatStop();

	var child1 = $("#chatChild1").val();
	var child2 = $("#chatChild2").val();

	ws = new WebSocket("ws://127.0.0.1:3080/chat/" + _.escape(child1) + '/' + _.escape(child2), ["cynja-chat"]);

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

	var chatMessage = $("#chatMessage").val();

	ws.send(chatMessage);
	$("chatMessage").val('');
}

</script>

</head>

<body>

<pre id="example">
 * CYNJA CLOUD CHAT - example parent/child data - <a href="https://github.com/neustarpc/cynja-cloud-chat">https://github.com/neustarpc/cynja-cloud-chat</a>
 * ================

 * Parents                                                      * Parents
 *   [=]!:uuid:1111 and [=]!:uuid:2222                          *   [=]!:uuid:5555 and [=]!:uuid:6666
 * have the following children                                  * have the following children
 *   [=]!:uuid:3333, [=]!:uuid:4444                             *   [=]!:uuid:7777, [=]!:uuid:8888, [=]!:uuid:9999
</pre>

<div id="chat">
<table>
<tr><td>From:</td><td><input type="text" id="chatChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>To:</td><td><input type="text" id="chatChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
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
<p class="heading">Establish Connection</p>
<table>
<tr><td>Child 1</td><td><input type="text" id="establishChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>Child 2</td><td><input type="text" id="establishChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
</table>
<button onclick="establish();">Establish</button>
</div>

<div>
<p class="heading">Approve Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="approveParent"></td><td class="example">e.g. [=]!:uuid:1111, and [=]!:uuid:6666</td></tr>
<tr><td>Child 1</td><td><input type="text" id="approveChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>Child 2</td><td><input type="text" id="approveChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
</table>
<button onclick="approve();">Approve</button>
</div>

<div>
<p class="heading">View Connections</p>
<table>
<tr><td>ParentOrChild</td><td><input type="text" id="viewParentOrChild"></td><td class="example">e.g. [=]!:uuid:3333, or [=]!:uuid:1111</td></tr>
</table>
<button onclick="view();">View</button>
</div>

<div>
<p class="heading">View Connection Log</p>
<table>
<tr><td>Parent</td><td><input type="text" id="logParent"></td><td class="example">e.g. [=]!:uuid:1111</td></tr>
<tr><td>Child 1</td><td><input type="text" id="logChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>Child 2</td><td><input type="text" id="logChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
</table>
<button onclick="log();">View Log</button>
</div>

<div>
<p class="heading">Block Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="blockParent"></td><td class="example">e.g. [=]!:uuid:1111</td></tr>
<tr><td>Child 1</td><td><input type="text" id="blockChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>Child 2</td><td><input type="text" id="blockChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
</table>
<button onclick="block();">Block</button>
</div>

<div>
<p class="heading">Unblock Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="unblockParent"></td><td class="example">e.g. [=]!:uuid:1111</td></tr>
<tr><td>Child 1</td><td><input type="text" id="unblockChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>Child 2</td><td><input type="text" id="unblockChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
</table>
<button onclick="unblock();">Unblock</button>
</div>

<div>
<p class="heading">Delete Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="deleteParent"></td><td class="example">e.g. [=]!:uuid:1111</td></tr>
<tr><td>Child 1</td><td><input type="text" id="deleteChild1"></td><td class="example">e.g. [=]!:uuid:3333</td></tr>
<tr><td>Child 2</td><td><input type="text" id="deleteChild2"></td><td class="example">e.g. [=]!:uuid:7777</td></tr>
</table>
<button onclick="delet();">Delete</button>
</div>

</body>

</html>
