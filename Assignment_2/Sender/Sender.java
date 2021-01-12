
import java.io.*;
import java.net.*;


public class Sender{
    public static void main(String[] args){
        if (args.length != 6){
            System.out.println("Missing arguments, program will stop");
            System.exit(0);
        }
        DatagramSocket dg;

        try{
            //Reads arguments
            String  addr = args[0];
            int receiverSocket = Integer.parseInt(args[1]);
            int senderSocket = Integer.parseInt(args[2]); 
            String fileName = args[3];
            int maxDataSize = Integer.parseInt(args[4]);
            int timeout = Integer.parseInt(args[5]);


            //Read File
            String file = readFile(fileName);
            byte[] b = new byte[1000];
            byte[] buffer;

           

            //init datagram
            dg = new DatagramSocket(null);
            DatagramPacket request = new DatagramPacket(b, b.length);
            dg.bind(new InetSocketAddress(addr,senderSocket));
            dg.setSoTimeout(timeout);
            
    
    
            //send request and start timer;
            System.out.println("Sending data from file: " + fileName + "\nAddress: " + addr + " using port: " + receiverSocket + "\nListening for ACKS on port: " + senderSocket);
            long startTimer = System.currentTimeMillis();

            for (int i = 0; i < (file.length() / maxDataSize) + 2; i++){
                if (i <= (file.length() / maxDataSize)) {
                    byte[] bufferSize = new byte[maxDataSize + 1];
                    int endIndex = i == file.length() / maxDataSize ? file.length() : maxDataSize * (i + 1);

                    for (int index = maxDataSize * i; index < endIndex; index++) {
                        bufferSize[maxDataSize + index - endIndex] = (byte) file.charAt(index);
                    }
                    bufferSize[maxDataSize] = (byte) (i % 2);

                    buffer = bufferSize;
                } else {
                    buffer = new byte[]{(byte) '\t', (byte) 4};
                }
                System.out.println("SENDING DATAGRAM");
                dg.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(addr), receiverSocket));

                try {
                    System.out.println("AWAITING RESPONSE (ACK)");
                    dg.receive(request);
                    int ackReceived = -1;

                
                    for (byte bte : request.getData()) {
                        String letter = String.valueOf((char) bte);
                        if (letter.equals("0") || letter.equals("1") || letter.equals("4"))
                            ackReceived = Integer.parseInt(letter);
                    }
                   
                    if (ackReceived != i % 2 && ackReceived != 4) {
                        System.out.println(" INVALID ACK RECEIVED - SENDING DATAGRAM");
                        i--;
                    } else {
                        System.out.println("VALID ACK");

                    }

                } catch (SocketTimeoutException exception) { 
                    System.out.println("TIMEOUT OCCURRED - RESEND DATAGRAM");
                    i--;
                    }

            }
            System.out.println("Transmission Time: "+ (System.currentTimeMillis() - startTimer));
            dg.close();
        }catch (Exception e){
            System.out.println("PARAMETERS ARE INCORRECT / UNEXPECTED ERROR - PROGRAM WILL NOW CLOSE");
            System.exit(0);
        }
    }
    public static String readFile(String file)  throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader (file));
            String line = null;
            String ls = System.getProperty("line.separator");
            while((line = reader.readLine()) != null) {
                line.strip();
                stringBuilder.append(line + " ");
                stringBuilder.append(ls);
            }
            reader.close();
        }  catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND - PROGRAM WILL TERMINTATE");
            System.exit(0);
        }
        return stringBuilder.toString();   
        
    }
}