import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
public class ClientHandler {

    private Socket socket = null;
    private PrintWriter pr = null;
    private BufferedReader bf = null;
    public void connect(String ip, int port) throws IOException{
        try {
            socket = new Socket(ip, port);
            pr = new PrintWriter(socket.getOutputStream(), true);
            bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }  catch (IOException e) {
            throw new IOException();   
        }
    }
    public void disconnect() throws IOException{
        bf.close();
        pr.close();
        socket.close();
    }
    public boolean isConnected(){
        try{
            pr.println("Are you there?");
            return bf.readLine().equals("yes");
        }catch(NullPointerException | IOException e){
            return false;
        }
    }
    public String sendRequest(String request, String isbn, String title, String author, String publisher, int year, boolean all, boolean bibtex) throws IOException{
        String data = "";
        StringBuilder builder = new StringBuilder();

        if (all) {
            data += request + "\r\n" + "ALL";
        } else {
            data += request +  "\r\n" + "ISBN " + isbn + "\r\n" + "TITLE " + title + "\r\n" + "AUTHOR " + 
                author +  "\r\n"+ "PUBLISHER " + publisher + "\r\n" + "YEAR " + year + "\r\n";
        }
    
        pr.println(data + "\r\n" + "\\EOF");
        String serverMessage = "";
        String line;
    
        line = bf.readLine();
        while (line != null && !line.contains("\\EOF")) {
            serverMessage = serverMessage.concat(line + "\r\n");
            line = bf.readLine();

        }

        if (bibtex) {
            String[] splitMessage = serverMessage.split("\r\n");
            if (splitMessage.length > 2) {
                for (int i = 0; i < splitMessage.length; i++) {
                    String s = splitMessage[i];
                    String[] splitLine = s.split(" ");
                    if (splitLine[0].contains("ISBN:")) {
                        builder.append("@Book{\r\n\tISBN\t= \"" + s.substring(splitLine[0].length()).trim() + "\",\n");
                    }
                    if (splitLine[0].contains("TITLE:")) {
                        builder.append("\tTITLE\t= \"" + s.substring(splitLine[0].length()).trim() + "\",\r\n");
                    }
                    if (splitLine[0].contains("AUTHOR:")) {
                        builder.append("\tAUTHOR\t= \"" + s.substring(splitLine[0].length()).trim() + "\",\r\n");
                    }
                    if (splitLine[0].contains("PUBLISHER:")) {
                        builder.append("\tPUBLISHER\t= \"" + s.substring(splitLine[0].length()).trim() + "\",\r\n");
                    }
                    if (splitLine[0].contains("YEAR:")) {
                        if (s.substring(splitLine[0].length()).trim().equals("0"))
                            builder.append("\tYEAR\t= \"No Value\",\r\n}\r\n");
                        else
                            builder.append("\tYEAR\t= \"" + s.substring(splitLine[0].length()).trim() + "\",\r\n}\r\n");
                    }
                }
                serverMessage = builder.toString();
            }
        }
        

        return serverMessage;
    }



    
}
