<img src="http://neustarpc.github.com/neustar-clouds/images/logo.png"><br>

This is a prototype of a HTTP proxy server that allows guardians to manage HTTP accesses via browsers
of dependents via XDI graphs.

### How to build

First, [XDI2](http://github.com/projectdanube/xdi2) and [LittleProxy](http://github.com/adamfisk/LittleProxy) need to be build.

After that, just run

    mvn clean install

To build all components.

### How to run

    mvn sprint-boot:run

Then the proxy status page is available at

    http://localhost:8080/proxies/status

which would provide the sample output:

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Fri, 19 Sep 2014 15:37:06 GMT

    {
      "id" : 2,
      "time_started" : "2014-09-19T15:36:54.478+0000"
    }
