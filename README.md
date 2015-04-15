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

The API is available at:

	http://localhost:3080/establish
	http://localhost:3080/approve
	http://localhost:3080/view
	http://localhost:3080/block
	http://localhost:3080/unblock
	http://localhost:3080/delete
	ws://localhost:3080/chat/{fromchild}/{tochild}

TODO: Document the API
