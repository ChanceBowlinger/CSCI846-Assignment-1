# CSCI 846: Distributed Systems - Proxy Server

## Requirements
- JDK v25
- JDK configured in the system's varibles

## Run Project
To redirect all the browser requests, enable the proxy setting on your machine.
### For Windows Users
1. Open Settings > Network Settings.
2. Enable Manual Proxy settings.
3. Provide the Proxy server IP address (127.0.0.1) and the Port of your choice for the proxy server and save the changes.
### For Mac Users
1. Open Settings > Network Settins.
2. Click on Details and the Proxies.
3. Provide the Proxy server IP address (127.0.0.1) and the Port of your choice for the proxy server and save the changes.

This project can be Run by either using the Command Line Interface or an IDE like Netbeans.

### Command Line Manual
1. Open a terminal.
2. Navigate to the root folder of the project, i.e; `CSCI846-Assignment-1/`
3. Compile all the Java source code into class files using the `javac` command. 
<br> ````javac -d bin src/main/java/Proxy/*.java````
    - `-d` will indicate that the project file has to work with multiple java files.
4. Run the compiled code with `java` command with the port number dedicated while setting up the proxy settings.
<br>````java -cp bin Proxy.ProxyServer 2000````

### IDE(Netbeans) Manual
1. Open Netbeans IDE.
2. Click on File > Open Project and then select the project root folder.
3. Open Project Properties and then navigate to the Run tab.
4. In the Arguments, add the dedicated port number for the proxy server.
5. Hit Run from the top bar

Now you have a proxy server that logs the **HTTP GET** requests and serves the chached responses to reduce latency and bandwidth. 
