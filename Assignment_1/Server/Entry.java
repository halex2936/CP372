import java.io.* ;
import java.net.* ;
import java.util.* ;

public class Entry{
	private int year = 0;
	private String title = "", author = "",publisher = "", isbn = "";

	public void setISBN(String ISBN){
		this.isbn = ISBN;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public void setAuthor(String author){
		this.author = author;
	}
	public void setPublisher(String publisher){
		this.publisher = publisher;
	}
	public void setYear(int year){
		this.year = year;
	}
	public String getTitle(){
		return this.title;
	}
	public String getAuthor(){
		return this.author;
	}
	public String getPublisher(){
		return this.publisher;
	}
	public String getISBN(){
		return this.isbn;
	}
	public int getYear(){
		return this.year;
	}
	@Override
    public String toString() {
        return "ISBN: " + isbn +
                "\nTITLE: " + title +
                "\nAUTHOR: " + author +
                "\nPUBLISHER: " + publisher +
                "\nYEAR: " + year+ "\r\n";
    }

}
