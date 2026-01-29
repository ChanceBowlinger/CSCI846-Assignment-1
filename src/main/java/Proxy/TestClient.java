package Proxy;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestClient {
    public static void main(String[] args) throws Exception {
        int port = 2000;
        try (Socket s = new Socket("127.0.0.1", port)) {
            System.out.println("Connected: " + s);
            OutputStream out = s.getOutputStream();
            out.write("hello\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            
          
        }
        System.out.println("Closed client");
    }
}
