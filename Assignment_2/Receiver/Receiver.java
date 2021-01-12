
import java.io.*;
import java.net.*;
import java.util.* ;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.FileWriter;


public class Receiver{
	private JFrame connectFrame;
	private JPanel panel;
	private JButton receiveB;
	private JLabel rPortL,sPortL, ipL, fileNameL, requestL, packetL;
	private JTextField ipTF, rPortTF, sPortTF, fileNameTF;
	private JTextArea output;
	private JCheckBox reliableCB;
    private int inOrderPacketCount, packetCount, totalPacketCount;
    
    public static void main(String[] args){
        new Receiver();
    }


    public Receiver() {
        initGUI();
    }
    public void initGUI(){
		connectFrame = new JFrame();
		panel = new JPanel();
        rPortTF = new JTextField("4455");
        sPortTF = new JTextField("3321");
        ipTF = new JTextField("localhost");
        fileNameTF = new JTextField("output.txt");
        rPortL = new JLabel("Receiver Port");
        sPortL = new JLabel("Sender Port");
        ipL = new JLabel("IP Address");
        fileNameL = new JLabel("File Name");
        inOrderPacketCount = 0;
        totalPacketCount = 0;
        packetCount = 0;
		receiveB = new JButton("RECEIVE");
        packetL = new JLabel("0");
        reliableCB = new JCheckBox("TURN ON RELIABLE?");
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

        receiveB.addActionListener(new receiveEvent());

		panel.setLayout(grid);

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("Group 50"),c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(ipL,c);

		c.gridx = 1;
		c.gridy = 1;
		ipTF.setPreferredSize(new Dimension(150,30));
		panel.add(ipTF,c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(rPortL,c);

		c.gridx = 1;
		c.gridy = 2;
		rPortTF.setPreferredSize(new Dimension(150,30));
        panel.add(rPortTF,c);
        
		c.gridx = 0;
		c.gridy = 3;
		panel.add(sPortL,c);

		c.gridx = 1;
		c.gridy = 3;
		sPortTF.setPreferredSize(new Dimension(150,30));
        panel.add(sPortTF,c);
        
        c.gridx = 0;
		c.gridy = 4;
		panel.add(fileNameL,c);

		c.gridx = 1;
		c.gridy = 4;
		fileNameTF.setPreferredSize(new Dimension(150,30));
        panel.add(fileNameTF,c);


        c.gridx = 2;
        c.gridy = 2;
        panel.add(new JLabel("TOTAL PACKET COUNT: "),c);

        c.gridx = 3;
        c.gridy = 2;
        panel.add(packetL,c);

        c.gridx = 2;
        c.gridy = 3;
        panel.add(reliableCB,c);

		c.gridx = 2;
		c.gridy = 4;
		receiveB.setPreferredSize(new Dimension(150,30));
		panel.add(receiveB,c);
        
        connectFrame.setSize(new Dimension(500,300));
		connectFrame.add(panel);
		connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectFrame.setTitle("CP372 A2 Alexander Hoang");
		connectFrame.setVisible(true);
        connectFrame.setResizable(false);
		connectFrame.setLocationRelativeTo(null);
    }


    private class receiveEvent implements ActionListener{
        DatagramSocket dg;
		public void actionPerformed(ActionEvent e){
            if (receiveB.getText().equals("RECEIVE")){
                try{

                    // PREVENT GUI FROM HANGING
                    new SwingWorker<Void, Void>() {
                        @Override
                        
                        protected Void doInBackground() {
                            try{
                                receiveHandler();
                            }catch(IOException ex){
                                //ignore
                            }
                            return null;
                        }
                    }.execute();
                    
                        
                    //implement reliable
                    reliableCB.setEnabled(false);
                    receiveB.setText("END");
                }catch (Exception ec){
                    System.out.println("AN UNEXPECTED ERROR HAS OCCURRED");
                }
            }
            else{
                reliableCB.setEnabled(true);
                receiveB.setText("RECEIVE");
                dg.close();
            }
        }
        private void writeFile(String fileName, String response){
            try{
                File f = new File(fileName);
                if (f.createNewFile()) {
                    System.out.println("Created a new file");
                }
                FileWriter fw = new FileWriter(fileName);
                fw.write(response);
                fw.close();
                f = new File("InOrderPacketCount.txt");
                if (f.createNewFile()) {
                    System.out.println("Packet Count file created");
                }
                fw = new FileWriter("InOrderPacketCount.txt");
                fw.write(inOrderPacketCount + "");
                fw.close();
            }catch(IOException e){
                System.out.println("AN ERROR OCCURRED TRYING TO CREATE A FILE");
            }
        }
        private void receiveHandler() throws IOException{
            String addr = ipTF.getText();
            int receiverSocket = Integer.parseInt(rPortTF.getText());
            int senderSocket = Integer.parseInt(sPortTF.getText()); 
            String fileName = fileNameTF.getText();
            boolean reliable = reliableCB.isSelected();

            System.out.println("Receiving on address: " + addr + " : " + receiverSocket + "\nOutput File Name: " + fileName + "\nACKS Send to: " + senderSocket);

            StringBuilder replyData = new StringBuilder();
            
            dg = new DatagramSocket(null);
            dg.bind(new InetSocketAddress(addr, receiverSocket));
                


            //receive
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            StringBuilder finalData = new StringBuilder();
 
             while (true){
                try{                    
                        
                    System.out.println("WAITING FOR DATA");

                    dg.receive(reply);
                    packetCount++;
                    
                    if (packetCount % 10 != 0 || reliable){
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i< reply.getLength(); i++){
                            if (reply.getData()[i] >= 9){
                                sb.append((char) reply.getData()[i]);
                            }                            
                        }
                        finalData.append(sb.toString());
    
                        int sequenceNumber = reply.getData()[reply.getLength() - 1];
        
                        //EOT datagram
                        if (sb.toString().contains("\t") && sequenceNumber == 4){
                            writeFile(fileName, finalData.toString());
                            finalData = new StringBuilder();
                            packetL.setText(totalPacketCount + "");
                            inOrderPacketCount = 0;
                        }
                        else{
                            inOrderPacketCount++;
                            totalPacketCount++;
                            packetL.setText(inOrderPacketCount + "");
                        }
                            
                        String ack = "ACK "  + sequenceNumber;
                        System.out.println("DATA RECEIVED - SENDING ACK no " + sequenceNumber);
                        DatagramPacket request = new DatagramPacket(ack.getBytes(),ack.getBytes().length,InetAddress.getByName(addr),senderSocket);
                        dg.send(request);
                                            
                        
                    }
                    else{
                        System.out.println("SIMULATING UNRELIABLE TRANSFER - SENDER PROGRAM WILL NOW HANG UNTIL TIMEOUT OCCURS");
                    }
    

                }catch (IOException ex){
                    break;
               }

            }

        }
    }
    
}