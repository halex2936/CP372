import java.io.*;
import java.net.*;
import java.util.*;

public class Server{

    public static void main(String[] args)throws IOException{
        ServerSocket serverSocket = null;
        Socket connection = null;
        int port;

        if (args.length == 0)
            port = 24;
        else
            port = Integer.parseInt(args[0]);

		ArrayList<Entry> bibliography = new ArrayList<>();
		
		try {
			serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+port);
            System.exit(1);
        }

		System.out.println("SERVER ACTIVE. PORT: "+ port);
		
		while (true) {
			// Listen for a TCP connection request.
			connection = serverSocket.accept();

			// Create a new thread to process the request.
			ServerProtocol thread = new ServerProtocol(Thread.activeCount() + "", connection, bibliography);
			
			// Start the thread.
			thread.start();
		
			
			
		}

	}

   
}

