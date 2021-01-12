import java.io.* ;
import java.net.* ;
import java.util.* ;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

final class Client{
	private ClientHandler clientHandler;
	private JFrame connectFrame, inputFrame, msgFrame;
	private JPanel panel,inputPanel, msgPanel, checkboxPanel;
	private JButton sendB,connectB,exitB;
	private JLabel label,isbnL,titleL,authorL,publisherL,yearL,portL,ipL, msgL, requestL;
	private JTextField isbnTF,titleTF,authorTF,publisherTF,yearTF, ipTF, portTF;
	private JComboBox<String> requestBox;
	private JTextArea output;
	private JCheckBox checkbox,bibtexcb;

	public static void main(String[] args) throws IOException {
		new Client();
		
    }
    // Constructor
    public Client() {
		connectFrame = new JFrame();
		inputFrame = new JFrame();
		panel = new JPanel();
		portTF = new JTextField("24");
		ipTF = new JTextField("localhost");
		portL = new JLabel("Port");
		ipL = new JLabel("IP");
		connectB = new JButton("CONNECT");
		exitB = new JButton("EXIT");
		clientHandler = new ClientHandler();
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		initGUI();
		connectB.addActionListener(new connectEvent());
		exitB.addActionListener(new exitEvent());

		panel.setLayout(grid);

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("Client Login"),c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(ipL,c);

		c.gridx = 1;
		c.gridy = 1;
		ipTF.setPreferredSize(new Dimension(150,30));
		panel.add(ipTF,c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(portL,c);

		c.gridx = 1;
		c.gridy = 2;
		portTF.setPreferredSize(new Dimension(150,30));
		panel.add(portTF,c);

		c.gridx = 1;
		c.gridy = 4;
		connectB.setPreferredSize(new Dimension(150,30));
		panel.add(connectB,c);

	
		connectFrame.add(panel);
		connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectFrame.setTitle("LOGIN");
		connectFrame.setVisible(true);
		connectFrame.pack();
		connectFrame.setLocationRelativeTo(null);
	}

	private boolean validISBN(String ISBN){
		int total = 0;
		for (int i = 0; i < ISBN.length() - 1; i++){
			int value = Character.getNumericValue(ISBN.charAt(i));
			if (i%2 == 0)
				total += value;
			else{
				total += 3*value;
			}
		}

		return (((total) + Character.getNumericValue(ISBN.charAt(ISBN.length() - 1))) % 10) == 0;
	}
	private boolean connect(boolean success){
		try{
			clientHandler.connect(ipTF.getText(), Integer.parseInt(portTF.getText()));
			} catch(IOException exception){
				success= false;
				JOptionPane.showMessageDialog(null,"IP/PORT IS INCORRECT","ERROR",JOptionPane.PLAIN_MESSAGE);
		}
		return success;
	}
	private class connectEvent implements ActionListener{
		public void actionPerformed(ActionEvent e){

			boolean bool = true;

			bool = connect(bool);
			if (bool){
				exitB.setEnabled(true);
				connectB.setEnabled(false);
				connectFrame.setVisible(false);
				inputFrame.setVisible(true);
				inputFrame.setLocationRelativeTo(null);
				inputFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				//If Person clicks x button it handles the GUI here
				inputFrame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent event) {
						try{
							clientHandler.disconnect();
						}catch(Exception exception){
							exception.printStackTrace();
						}finally{
							connectFrame.setVisible(true);
							connectB.setEnabled(true);
							inputFrame.setVisible(false);

						}
					}
				});
			}
		

		}
	}
	private class exitEvent implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
				clientHandler.disconnect();
			}catch(Exception exception){
				exception.printStackTrace();
			}finally{
				connectFrame.setVisible(true);
				connectB.setEnabled(true);
				inputFrame.setVisible(false);
			}
	
		}
	}
	private void initGUI(){
		isbnL = new JLabel("ISBN: ");
		authorL  = new JLabel("AUTHOR:");
		publisherL =  new JLabel("PUBLISHER: ");
		String[] s = {"SUBMIT","UPDATE","GET","REMOVE"};
		yearL = new JLabel("YEAR: ");
		sendB = new JButton("SEND");
		requestL = new JLabel("REQUEST: ");
		requestBox = new JComboBox<>(s);
		output = new JTextArea();
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		inputPanel = new JPanel();
		checkboxPanel = new JPanel();
		isbnTF = new JTextField();
		authorTF = new JTextField();
		publisherTF = new JTextField();
		yearTF = new JTextField();
		titleTF = new JTextField();
		titleL = new JLabel("TITLE: ");
		checkbox = new JCheckBox();
		bibtexcb = new JCheckBox();
		//GUI For after login
		sendB.addActionListener(new requestEvent());
		inputPanel.setLayout(grid);
		checkbox.setEnabled(false);
		bibtexcb.setEnabled(false);


		c.gridx = 1;
		c.gridy = 0;
		inputPanel.add(new JLabel("CP372 ASSIGNMENT 1"),c);


		
		c.gridx = 0;
		c.gridy = 1;
		
		inputPanel.add(new JLabel("Alexander Hoang"),c);

		c.gridx = 0;
		c.gridy = 2;
		
		inputPanel.add(new JLabel("Group 50"),c);

		
		c.gridx = 1;
		c.gridy = 2;
		
		inputPanel.add(new JLabel("OUTPUT"),c);

		c.gridx = 1;
		c.gridy = 3;
		output.setPreferredSize(new Dimension(400,200));
		output.setEditable(false);
		output.setLineWrap(true);
		output.setBorder(BorderFactory.createLineBorder(Color.black));
		inputPanel.add(output,c); 

		c.gridx = 0;
		c.gridy = 4;
		inputPanel.add(requestL,c);

		c.gridx = 1;
		c.gridy = 4;
		requestBox.setPreferredSize(new Dimension(250,30));
		requestBox.addActionListener(new requestBoxEvent());
		inputPanel.add(requestBox,c);
		

		c.gridx = 0;
		c.gridy = 5;
		inputPanel.add(isbnL,c);

		c.gridx = 1;
		c.gridy = 5;
		isbnTF.setPreferredSize(new Dimension(250,30));
		inputPanel.add(isbnTF,c);

		//inputPanel.add((new JLabel("")));

		c.gridx = 0;
		c.gridy = 6;
		inputPanel.add(titleL,c);

		c.gridx = 1;
		c.gridy = 6;
		titleTF.setPreferredSize(new Dimension(250,30));
		inputPanel.add(titleTF,c);

		//inputPanel.add((new JLabel("")));
		c.gridx = 0;
		c.gridy = 7;
		inputPanel.add(authorL,c);

		c.gridx = 1;
		c.gridy = 7;
		authorTF.setPreferredSize(new Dimension(250,30));
		inputPanel.add(authorTF,c);

		//inputPanel.add((new JLabel("")));
		c.gridx = 0;
		c.gridy = 8;
		inputPanel.add(publisherL,c);

		c.gridx = 1;
		c.gridy = 8;
		publisherTF.setPreferredSize(new Dimension(250,30));
		inputPanel.add(publisherTF,c);



		c.gridx = 0;
		c.gridy = 9;
		inputPanel.add(yearL,c);

		c.gridx = 1;
		c.gridy = 9;
		yearTF.setPreferredSize(new Dimension(250,30));
		inputPanel.add(yearTF,c);
		//checkbox
		c.gridx = 1;
		c.gridy = 0;
		checkboxPanel.add(new JLabel("BIBTEX"),c);

		c.gridx = 1;
		c.gridy = 1;
		checkboxPanel.add(bibtexcb,c);

		c.gridx = 0;
		c.gridy = 0;
		checkboxPanel.add(new JLabel("ALL"),c);

		c.gridx = 0;
		c.gridy = 1;
		checkboxPanel.add(checkbox,c);

		c.gridx = 2;
		c.gridy = 7;
		inputPanel.add(checkboxPanel,c);
		//BUTTON CONSTRAINTS
		c.gridx = 2;
		c.gridy = 8;
		exitB.setPreferredSize(new Dimension(100,30));
		inputPanel.add(exitB,c);

		c.gridx = 2;
		c.gridy = 9;
		sendB.setPreferredSize(new Dimension(100,30));
		inputPanel.add(sendB,c);

		

		//Puts the panel into the frame
		inputFrame.add(inputPanel);
		inputFrame.setTitle("ASSIGNMENT 1: MAIN CLIENT");
		inputFrame.pack();
	}
	private class requestBoxEvent implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String request = String.valueOf(requestBox.getSelectedItem()).trim();
			if (request.equals("GET")) {
				checkbox.setEnabled(true);
				bibtexcb.setEnabled(true);
			} else {
				checkbox.setEnabled(false);
				checkbox.setSelected(false);
				bibtexcb.setEnabled(false);
				bibtexcb.setSelected(false);
				isbnTF.setEnabled(!checkbox.isSelected());
				titleTF.setEnabled(!checkbox.isSelected());
				authorTF.setEnabled(!checkbox.isSelected());
				publisherTF.setEnabled(!checkbox.isSelected());
				yearTF.setEnabled(!checkbox.isSelected());
			}
		}
    }
	private class requestEvent implements ActionListener{
		public void actionPerformed(ActionEvent e){



			if (clientHandler.isConnected()){
				try {
					output.setText("");
					String isbn;
					String title = titleTF.getText().trim();
					String author = authorTF.getText().trim();
					String publisher= publisherTF.getText().trim();
					String request = String.valueOf(requestBox.getSelectedItem()).trim();
					int year = 0;
					
					if (yearTF.getText().length() > 0){
						try {
							year = Integer.parseInt(yearTF.getText());
						} catch (NumberFormatException exception) {
							JOptionPane.showMessageDialog(null, "INVALID YEAR", "ERROR", JOptionPane.PLAIN_MESSAGE);
							return;
						}
					}
					
					if (request == "GET" && checkbox.isSelected()) {
						output.setText(clientHandler.sendRequest("GET", "", "", "", "", 0, true,bibtexcb.isSelected()));
						try{
							clientHandler.disconnect();
						}catch(Exception exception){
							exception.printStackTrace();
						}
						connect(true);
						return;
					}
	
					isbn = isbnTF.getText().replace("-", "").trim();
	
					if ((request.equals("SUBMIT") || request.equals("UPDATE")) ){
						if (isbn.length() == 0) {
							JOptionPane.showMessageDialog(null, "ENTER AN ISBN", "ERROR", JOptionPane.PLAIN_MESSAGE);
							return;
						}
					}
					if ((request.equals("GET") || request.equals("REMOVE")) ){
						if (isbn.length() == 0 && title.length() == 0 && author.length() == 0 && publisher.length() == 0 && year == 0) {
							JOptionPane.showMessageDialog(null, "FILL IN A FIELD OR ALL", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					if (request.equals("REMOVE") ) {
						int response = JOptionPane.showConfirmDialog(null, "CONFIRM REMOVE ACTION");
						if (response == JOptionPane.NO_OPTION || response == JOptionPane.CANCEL_OPTION) {
							return;
						}
					}
	
		
					if (isbn.length() == 13 ) {
						
						if (validISBN(isbn) ) {
							output.setText(clientHandler.sendRequest(request, isbn, title, author, publisher, year, false,bibtexcb.isSelected()));
							try{
								clientHandler.disconnect();
							}catch(Exception exception){
								exception.printStackTrace();
							}
							connect(true);
							return;
						}
					}
					
					if (isbn.length() == 0){
						output.setText(clientHandler.sendRequest(request, isbn, title, author, publisher, year, false,bibtexcb.isSelected()));
						try{
							clientHandler.disconnect();
						}catch(Exception exception){
							exception.printStackTrace();
						}
						connect(true);
					}
					else{
						JOptionPane.showMessageDialog(null,"INVALID ISBN","ERROR",JOptionPane.PLAIN_MESSAGE);
					}
				} 
				catch (NumberFormatException exception) {
					JOptionPane.showMessageDialog(null, "Invalid ISBN", "ERROR", JOptionPane.PLAIN_MESSAGE);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}else{
				try{
					clientHandler.disconnect();
				}catch(Exception exception){
					exception.printStackTrace();
				}finally{
					connectFrame.setVisible(true);
					connectB.setEnabled(true);
					inputFrame.setVisible(false);
				}
			}
			
		}
	}

}



