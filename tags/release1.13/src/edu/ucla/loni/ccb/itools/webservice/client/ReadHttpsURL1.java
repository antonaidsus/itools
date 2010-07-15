package edu.ucla.loni.ccb.itools.webservice.client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;

public class ReadHttpsURL1 {
   static final int HTTPS_PORT = 443; 

   public static void main(String argv[]) throws Exception { 
      if (argv.length != 1) { 
         System.out.println("Usage: java ReadHttpsURL1 ");
         System.exit(0);
      }  
       
      // Get a Socket factory 
      SocketFactory factory = SSLSocketFactory.getDefault(); 
 
      // Get Socket from factory 
      Socket socket = factory.createSocket(argv[0], HTTPS_PORT); 
       
      BufferedWriter out = new BufferedWriter(new 
      		OutputStreamWriter(socket.getOutputStream()));
      BufferedReader in = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));
      out.write("GET / HTTP/1.0\n\n");
      out.flush();

      String line;
      StringBuffer sb = new StringBuffer();
      while((line = in.readLine()) != null) {
         sb.append(line);
      }
      out.close();
      in.close();
      System.out.println(sb.toString());
   }
}
