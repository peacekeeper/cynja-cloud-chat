<html>

<head>

<title>Cynja Cloud Chat</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

<link rel="stylesheet" href="css/style.css" TYPE="text/css" MEDIA="screen" />

<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/lodash.compat.min.js"></script>

<script type="text/javascript">

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

$(document).ready(function() {
	var xdi = getParameterByName('xdi');
	if (! xdi) return;
	alert("Received link contract!");
	$("#appSessionLinkContract").val(xdi);
});

function connecttocloud() {

	var cloudName = $("#connectCloudName").val().trim(); if (! cloudName) { alert("Please enter \"Cloud Name\""); return; }
	var appCloudNumber = $("#connectAppCloudNumber").val().trim(); if (! appCloudNumber) { alert("Please enter \"App Cloud Number\""); return; }

	$.ajax({
	    url: '/2/connecttocloud',
	    type: 'POST',
	    data: 'cloudName=' + encodeURIComponent(cloudName) + '&' + 'appCloudNumber=' + encodeURIComponent(appCloudNumber),
	    success: function(data) { 
	    	alert('success: ' + JSON.stringify(data)); 
	    	$("#appSessionCloudNumber").val(data.appSessionCloudNumber);
	    	$("#appSessionPrivateKey").val(data.appSessionPrivateKey);
	    	$("#appConnectRequestUri").val(data.appConnectRequestUri);
	    	},
	    error: function(msg) { alert('error: ' + JSON.stringify(msg)); }
	});
}

function connectstartauth() {

	var appConnectRequestUri = $("#appConnectRequestUri").val().trim(); if (! appConnectRequestUri) { alert("Please enter \"App Connect Request URI\""); return; }

	window.location = appConnectRequestUri;
}

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

<pre id="example">
 * CYNJA CLOUD CHAT V2 - <a href="https://github.com/peacekeeper/cynja-cloud-chat">https://github.com/peacekeeper/cynja-cloud-chat</a>
 * ================

 *
 *
 *

</pre>

<div id="connect">
<p class="heading">Connect To Cloud</p>
<table>
<tr><td>Cloud Name:</td><td><input size="55" size="55" type="text" id="connectCloudName" value="=alice"> &lt;-- user types this</td></tr>
<tr><td>App Cloud Number:</td><td><input type="text" size="55" id="connectAppCloudNumber" value="*!:uuid:7bdc5008-10d5-49dc-b8b5-05cee13a465a"> &lt;-- this is static *cynjaspace</td></tr>
<tr><td><button onclick="connecttocloud();">connecttocloud()</button></td></tr>
<tr><td>App Session Cloud Number:</td><td><input size="55" type="text" id="appSessionCloudNumber"> &lt;-- app must remember this! (ASCN)</td></tr>
<tr><td>App Session Private Key:</td><td><input size="55" type="text" id="appSessionPrivateKey"> &lt;-- app must remember this! (ASPK)</td></tr>
<tr><td>App Connect Request URI:</td><td><input size="55" type="text" id="appConnectRequestUri"> &lt;-- app opens this in browser!</td></tr>
<tr><td><button onclick="connectstartauth();">Start Auth</button></td></tr>
<tr><td>App Session Link Contract:</td><td><input size="55" type="text" id="appSessionLinkContract"> &lt;-- app must remember this! (ASLC)</td></tr>
</table>
</div>

<hr noshade>

<div id="chat">
<table>
<tr><td>Child 1:</td><td><input type="text" id="chatChild1"></td></tr>
<tr><td>Child 2:</td><td><input type="text" id="chatChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="chatASCN" size="5">&nbsp;ASPK: <input type="text" id="chatASPK" size="5">&nbsp;ASLC: <input type="text" id="chatASLC" size="5"></td></tr>
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
<tr><td>Child 2</td><td><input type="text" id="requestChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="requestASCN" size="5">&nbsp;ASPK: <input type="text" id="requestASPK" size="5">&nbsp;ASLC: <input type="text" id="requestASLC" size="5"></td></tr>
</table>
<button onclick="request();">Request</button>
</div>

<div>
<p class="heading">Approve Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="approveParent"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="approveChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="approveChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="approveASCN" size="5">&nbsp;ASPK: <input type="text" id="approveASPK" size="5">&nbsp;ASLC: <input type="text" id="approveASLC" size="5"></td></tr>
</table>
<button onclick="approve();">Approve</button>
</div>

<div>
<p class="heading">View Connections As Parent</p>
<table>
<tr><td>Parent</td><td><input type="text" id="viewasparentParent"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="viewasparentASCN" size="5">&nbsp;ASPK: <input type="text" id="viewasparentASPK" size="5">&nbsp;ASLC: <input type="text" id="viewasparentASLC" size="5"></td></tr>
</table>
<button onclick="viewasparent();">View As Parent</button>
</div>

<div>
<p class="heading">View Connections As Child</p>
<table>
<tr><td>Child</td><td><input type="text" id="viewaschildChild"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="viewaschildASCN" size="5">&nbsp;ASPK: <input type="text" id="viewaschildASPK" size="5">&nbsp;ASLC: <input type="text" id="viewaschildASLC" size="5"></td></tr>
</table>
<button onclick="viewaschild();">View As Child</button>
</div>

<div>
<p class="heading">View Connection Log</p>
<table>
<tr><td>Parent</td><td><input type="text" id="logsParent"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="logsChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="logsChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="logsASCN" size="5">&nbsp;ASPK: <input type="text" id="logsASPK" size="5">&nbsp;ASLC: <input type="text" id="logsASLC" size="5"></td></tr>
</table>
<button onclick="logs();">View Log</button>
</div>

<div>
<p class="heading">Block Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="blockParent"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="blockChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="blockChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="blockASCN" size="5">&nbsp;ASPK: <input type="text" id="blockASPK" size="5">&nbsp;ASLC: <input type="text" id="blockASLC" size="5"></td></tr>
</table>
<button onclick="block();">Block</button>
</div>

<div>
<p class="heading">Unblock Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="unblockParent"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="unblockChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="unblockChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="unblockASCN" size="5">&nbsp;ASPK: <input type="text" id="unblockASPK" size="5">&nbsp;ASLC: <input type="text" id="unblockASLC" size="5"></td></tr>
</table>
<button onclick="unblock();">Unblock</button>
</div>

<div>
<p class="heading">Delete Connection</p>
<table>
<tr><td>Parent</td><td><input type="text" id="deleteParent"></td></tr>
<tr><td>Child 1</td><td><input type="text" id="deleteChild1"></td></tr>
<tr><td>Child 2</td><td><input type="text" id="deleteChild2"></td></tr>
<tr><td colspan="2">ASCN: <input type="text" id="deleteASCN" size="5">&nbsp;ASPK: <input type="text" id="deleteASPK" size="5">&nbsp;ASLC: <input type="text" id="deleteASLC" size="5"></td></tr>
</table>
<button onclick="delet();">Delete</button>
</div>

</body>

</html>
