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
	HTTP POST:  http://localhost:3080/approve?parent=...&child1=...&child2=...
	HTTP POST:  http://localhost:3080/view?parentOrChild=...
	HTTP POST:  http://localhost:3080/log?parent=...&child1=...&child2=...
	HTTP POST:  http://localhost:3080/block?parent=...&child1=...&child2=...
	HTTP POST:  http://localhost:3080/unblock?parent=...&child1=...&child2=...
	HTTP POST:  http://localhost:3080/delete?parent=...&child1=...&child2=...
	WebSocket:  ws://localhost:3080/chat/{fromchild}/{tochild}

WebSocket sub-protcol: "cynja-chat"
