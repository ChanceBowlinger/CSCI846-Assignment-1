package Proxy;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;


// RequestHandler is thread that process requests of one client connection
public class RequestHandler extends Thread {

	
	Socket clientSocket;

	InputStream inFromClient;

	OutputStream outToClient;
	
	byte[] request = new byte[1024];

	
	private ProxyServer server;


	public RequestHandler(Socket clientSocket, ProxyServer proxyServer) {		
		this.clientSocket = clientSocket;
		

		this.server = proxyServer;

		try {
			clientSocket.setSoTimeout(2000);
			inFromClient = clientSocket.getInputStream();
			outToClient = clientSocket.getOutputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	@Override
	
	public void run() {
            
            /**
                     * To do
                     * Process the requests from a client. In particular, 
                     * (1) Check the request type, only process GET request and ignore others
                     * (2) Write log.
                     * (3) If the url of GET request has been cached, respond with cached content
                     * (4) Otherwise, call method proxyServertoClient to process the GET request
                     *
            */
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(inFromClient, "UTF-8"))
            ) {
                String firstLine = in.readLine();
                if (firstLine == null) return;
                
                if (firstLine.startsWith("GET")) {
                    StringBuilder requestBuilder = new StringBuilder();
                    requestBuilder.append(firstLine).append("\r\n");

                    // Reading all the headers
                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        requestBuilder.append(line).append("\r\n");
                    }

                    // End of the headers
                    requestBuilder.append("\r\n");

                    this.proxyServertoClient(requestBuilder.toString());
                }

            } catch (Exception e1){
                e1.printStackTrace();
            }
        }

	
	private void proxyServertoClient(String clientRequest) {

		FileOutputStream fileWriter = null;
//		Socket toWebServerSocket = null;
		InputStream inFromServer;
		OutputStream outToServer;
		
		// Create Buffered output stream to write to cached copy of file
		String fileName = "cached/" + generateRandomFileName() + ".dat";
		
		// to handle binary content, byte is used
		byte[] serverReply = new byte[4096];
		
			
		/**
		 * To do
		 * (1) Create a socket to connect to the web server (default port 80)
		 * (2) Send client's request (clientRequest) to the web server, you may want to use flush() after writing.
		 * (3) Use a while loop to read all responses from web server and send back to client
		 * (4) Write the web server's response to a cache file, put the request URL and cache file name to the cache Map
		 * (5) close file, and sockets.
		*/
                
		try {
                    BufferedReader reader = new BufferedReader(new StringReader(clientRequest));

                    // Parse first line
                    String firstLine = reader.readLine();
                    String[] parts = firstLine.split(" ");

                    String method = parts[0];
                    String fullURL = parts[1];
                    String version = parts[2];

                    URL url = new URL(fullURL);

                    String host = url.getHost();
                    int port = (url.getPort() == -1) ? 80 : url.getPort();
                    String path = url.getFile();

                    Socket toWebServerSocket = new Socket(host, port);

                    outToServer = toWebServerSocket.getOutputStream();
                    inFromServer = toWebServerSocket.getInputStream();

                    PrintWriter serverWriter =
                            new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(outToServer)), true);

                    // Send modified first line (relative path!)
                    serverWriter.println(method + " " + path + " " + version);

                    // Forward headers except Proxy-Connection
                    String headerLine;
                    while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {

                        if (!headerLine.toLowerCase().startsWith("proxy-connection")) {
                            serverWriter.println(headerLine);
                        }
                    }

                    serverWriter.println(); // end headers
                    serverWriter.flush();

                    // Forward server response back to client (RAW BYTES)
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inFromServer.read(buffer)) != -1) {
                        outToClient.write(buffer, 0, bytesRead);
                    }

                    outToClient.flush();

                    toWebServerSocket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

	}
	
	
	
	// Sends the cached content stored in the cache file to the client
	private void sendCachedInfoToClient(String fileName) {

		try {

			byte[] bytes = Files.readAllBytes(Paths.get(fileName));

			outToClient.write(bytes);
			outToClient.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			if (clientSocket != null) {
				clientSocket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	
	// Generates a random file name  
	public String generateRandomFileName() {

		String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
		SecureRandom RANDOM = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; ++i) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}

}
