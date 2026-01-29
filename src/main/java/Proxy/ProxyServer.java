package Proxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class ProxyServer {

	//cache is a Map: the key is the URL and the value is the file name of the file that stores the cached content
	Map<String, String> cache;
	
	ServerSocket proxySocket;

	String logFileName = "log.txt";

	public static void main(String[] args) {
            new ProxyServer().startServer(Integer.parseInt(args[0]));
	}

	void startServer(int proxyPort) {
            System.out.println("Made it to start");
            cache = new ConcurrentHashMap<>();

            // create the directory to store cached files. 
            File cacheDir = new File("cached");
            if (!cacheDir.exists() || (cacheDir.exists() && !cacheDir.isDirectory())) {
                    cacheDir.mkdirs();
            }

            /**
                     * To do:
                     * create a serverSocket to listen on the port (proxyPort)
                     * Create a thread (RequestHandler) for each new client connection 
                     * remember to catch Exceptions!
                     *
            */
            try{
                this.proxySocket = new ServerSocket();
                InetAddress localAddress = InetAddress.getByName("localhost");
                SocketAddress endpoint = new InetSocketAddress(localAddress, proxyPort);
                this.proxySocket.bind(endpoint);
                
                System.out.println("Listening on " + this.proxySocket.getLocalSocketAddress());

                while (true){
                    Socket proxyClient = this.proxySocket.accept();
                    System.out.println("Accepted from " + proxyClient.getRemoteSocketAddress());
                    new Thread(new RequestHandler(proxyClient, this)).start();
                }
                // Create thread
//                RequestHandler handler = new RequestHandler(proxyClient, this);
//                handler.run();
            }
            catch (Exception e) {
                System.out.println(e);
            }
	}



	public String getCache(String hashcode) {
		return cache.get(hashcode);
	}

	public void putCache(String hashcode, String fileName) {
		cache.put(hashcode, fileName);
	}

	public synchronized void writeLog(String info) {
		
			/**
			 * To do
			 * write string (info) to the log file, and add the current time stamp 
			 * e.g. String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			 *
			*/
	}

}
