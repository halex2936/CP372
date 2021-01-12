import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerProtocol extends Thread{
    private ArrayList<Entry> books;
    private final Socket connection;
    private BufferedReader bf;
    private PrintWriter pr;
    public ServerProtocol(String name, Socket connection, ArrayList<Entry> books){
        super(name);
        this.connection = connection;
        this.books = books;
    }
  
    public synchronized void run(){
        try{
            bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            pr = new PrintWriter(connection.getOutputStream(), true);
            listen();
            disconnect(); 
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void listen(){
        String lineIn, outRequest = "", inRequest = "";

        try {
            lineIn = bf.readLine();
            while (lineIn != null) {
                if (lineIn.equals("Are you there?")) {
                    outRequest = "yes";
                } else {
                    while (!lineIn.contains("\\EOF")) {
                        inRequest = inRequest.concat(lineIn + "\r\n");
                        lineIn = bf.readLine();
                    }
                    outRequest = processInput(inRequest.split("\n")).trim() + "\r\n\\EOF";
                }
                
                pr.println(outRequest);
                lineIn = bf.readLine();
                }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private String processInput(String[] message){
        String request = message[0].trim();
        String line = "";
        if (request.equals("REMOVE")){
            line = remove(message);

        }
        else if (request.equals("GET")){
            line = get(message);
            
        }
        else if (request.equals("UPDATE")){
            line = update(message);;
        }
        else if (request.equals("SUBMIT")){
            line = submit(message);
        }
        
        return line;
    }
    private String remove(String[] message){
        String serverReply;
        int count = 0;

        ArrayList<ArrayList<Entry>> booksList = new ArrayList<>();
        for (int i = 0; i < message.length; i++) {
            String line = message[i].trim();
            String[] words = line.split(" ");
            String value = line.substring(words[0].length()).trim();
            switch (words[0]) {
                case "ISBN":
                    if (value.length() > 0)
                        booksList.add(findEntry(books, "ISBN", value));
                    break;
                case "TITLE":
                    if (value.length() > 0)
                        booksList.add(findEntry(books, "TITLE", value));
                    break;
                case "AUTHOR":
                    if (value.length() > 0)
                        booksList.add(findEntry(books, "AUTHOR", value));
                    break;
                case "PUBLISHER":
                    if (value.length() > 0)
                        booksList.add(findEntry(books, "PUBLISHER", value));
                    break;
                case "YEAR":
                    if (Integer.parseInt(value) > 0)
                        booksList.add(findEntry(books, "YEAR", value));
                    break;
            }
        }
        ArrayList<Entry> repeat = libraryIntersection(booksList);
        if (repeat != null)
            for (Entry entry : repeat) {
                books.remove(entry);
                count++;
            }
        serverReply = "BOOKS REMOVED: " + count;
        return serverReply;
    }
    private String submit(String[] message){
        String serverReply;
        Entry entry = new Entry();
        for (int i = 0; i < message.length; i++) {
            String line = message[i].trim();
            String[] words = line.split(" ");
            String word;
            if (words[0].equals("ISBN")){
                if (findEntry(books, "ISBN", words[1]).size() != 0){
                    serverReply = "DUPLICATE ISBN";
                    return serverReply;
                }
                entry.setISBN(words[1]);
            }
            else if(words[0].equals("TITLE")){
                word = line.substring(words[0].length()).trim();
                entry.setTitle(word);
            }
            else if(words[0].equals("AUTHOR")){
                word = line.substring(words[0].length()).trim();
                entry.setAuthor(word);
            }
            else if(words[0].equals("PUBLISHER")){
                word = line.substring(words[0].length()).trim();
                entry.setPublisher(word);
            }
            else if(words[0].equals("YEAR")){
                word = line.substring(words[0].length()).trim();
                entry.setYear(Integer.parseInt(word));
            }
        }
        serverReply = "SUBMIT SUCCESS \n" + entry.toString();
        books.add(entry);
        return serverReply;
    }
    private String update(String[] message){
        String serverReply;
        Entry found = null;
        for (int i = 0; i < message.length; i++) {
            String line = message[i].trim();
            String[] words = line.split(" ");
            String value = line.substring(words[0].length()).trim();
            switch (words[0]) {
                case "ISBN":
                    if (findEntry(books, "ISBN",value).size() != 0)
                        found = findEntry(books, "ISBN",value).get(0);

                    break;
                case "TITLE":
                    if (found != null && value.length() > 0)
                        found.setTitle(value);
                    break;
                case "AUTHOR":
                    if (found != null && value.length() > 0)
                        found.setAuthor(value);

                    break;
                case "PUBLISHER":
                    if (found != null && value.length() > 0)
                        found.setPublisher(value);
                    break;
                case "YEAR":
                    if (found != null && value.length() > 0)
                        if (Integer.parseInt(value) != 0)
                            found.setYear(Integer.parseInt(value));
                    break;
            }
        }
        if (found != null)
            serverReply = "UPDATE SUCCESS\n" + found.toString();
        else
            serverReply = "BOOK NOT FOUND";
        return serverReply;
    }
    private String get(String[] message){
        StringBuilder serverReply = new StringBuilder();

        ArrayList<ArrayList<Entry>> bookList = new ArrayList<>();

        for (int i = 0; i < message.length; i ++) {
            String line = message[i].trim();
            String[] words = line.split(" ");
            String value = line.substring(words[0].length()).trim();
            switch (words[0]) {
                case "ALL":
                    if (books.size() == 0)
                        return "No books found.";
                    for (Entry entry : books) {
                        serverReply.append(entry.toString());
                        serverReply.append("\r\n");
                    }
                    return serverReply.toString();
                case "ISBN":
                    if (value.length() > 0)
                        bookList.add(findEntry(books, "ISBN", value));
                    break;
                case "TITLE":
                    if (value.length() > 0)
                        bookList.add(findEntry(books, "TITLE", value));
                    break;
                case "AUTHOR":
                    if (value.length() > 0)
                        bookList.add(findEntry(books, "AUTHOR", value));
                    break;
                case "PUBLISHER":
                    if (value.length() > 0)
                        bookList.add(findEntry(books, "PUBLISHER", value));
                    break;
                case "YEAR":
                    if (Integer.parseInt(value) > 0)
                        bookList.add(findEntry(books, "YEAR", value));
                    break;
            }
        }
        ArrayList<Entry> libraryIntersection = libraryIntersection(bookList);
        if (libraryIntersection == null)
            return "BOOKS NOT FOUND";
        else if (libraryIntersection.size() == 0)
            return "BOOKS NOT FOUND";
        for (Entry entry : libraryIntersection) {
            serverReply.append(entry.toString());
            serverReply.append("\r\n");
        }

        return serverReply.toString();
    }
    private void disconnect(){

        try{
            pr.close();
            bf.close();
            connection.close();
         }catch(Exception e){
            //ignore
         }

        
    }

    public static ArrayList<Entry> libraryIntersection(ArrayList<ArrayList<Entry>> bookList) {
        ArrayList<Entry> libraryIntersection = null;

        for (ArrayList<Entry> books : bookList) {
            libraryIntersection = libraryIntersection == null ? books : libraryIntersection;

            if (libraryIntersection == null) break;

            if (books != null)
                libraryIntersection.retainAll(books);

            else {
                libraryIntersection = null;
                break;
            }
        }

        return libraryIntersection;
    }

    public static ArrayList<Entry> findEntry(ArrayList<Entry> books, String attributeType, String searchValue) {
        ArrayList<Entry> library = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Entry entry = books.get(i);
            if (attributeType.equals("ISBN")) {
                if (entry.getISBN().equals(searchValue))
                    library.add(entry);
            }
            else if (attributeType.equals("TITLE")) {
                if (entry.getTitle().equals(searchValue))
                    library.add(entry);
            }
            else if (attributeType.equals("AUTHOR")) {
                if (entry.getAuthor().equals(searchValue))
                    library.add(entry);
            }
            else if (attributeType.equals("PUBLISHER")){
                if (entry.getPublisher().equals(searchValue))
                    library.add(entry);
            }
            else if (attributeType.equals("YEAR")) {
                if (Integer.toString(entry.getYear()).equals(searchValue))
                    library.add(entry);
            }

        }

        return library;
    }

}
