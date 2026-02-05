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
            cache = new ConcurrentHashMap<>();

            // create the directory to store cached files. 
            File cacheDir = new File("cached");
            if (!cacheDir.exists() || (cacheDir.exists() && !cacheDir.isDirectory())) {
                    cacheDir.mkdirs();
            }

            try{
                this.proxySocket = new ServerSocket();
                InetAddress localAddress = InetAddress.getByName("localhost");
                SocketAddress endpoint = new InetSocketAddress(localAddress, proxyPort);
                this.proxySocket.bind(endpoint);
                
                System.out.println("Listening on " + this.proxySocket.getLocalSocketAddress());

                while (true){
                    Socket proxyClient = this.proxySocket.accept();
                    new Thread(new RequestHandler(proxyClient, this)).start();
                }
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
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String browserIP = info.substring(info.indexOf(";") + 2); // Grab everything after ";" delimiter and first "/" character
            String browserURL = info.substring(info.indexOf("http:"), info.indexOf("HTTP") - 1); // may change if HTTP is stripped later
            String logString = timeStamp + "  " + browserIP  + "  " + browserURL;
            
            // Log in append mode
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
                writer.write(logString);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
	}

}
