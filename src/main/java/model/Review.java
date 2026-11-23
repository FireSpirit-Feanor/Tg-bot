package model;  //

import java.time.LocalDateTime;  

public class Review {  

    private final String bookTitle;  
    private final String author;     
    private final String text;       
    private final String date;       

    
    public Review(String bookTitle, String author, String text) {
        this.bookTitle = bookTitle;  
        this.author = author;        
        this.text = text;            
        this.date = LocalDateTime.now().toString(); 
    }

    public String getBookTitle() { return bookTitle; }  
    public String getAuthor() { return author; }        
    public String getText() { return text; }          
    public String getDate() { return date; }   

    @Override
    public String toString() {
        return "📖 " + bookTitle + "\n" +  
               "👤 " + author + " (" + date + ")\n" +
               "💬 " + text;
    }
}