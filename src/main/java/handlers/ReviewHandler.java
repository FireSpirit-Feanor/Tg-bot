package handlers; 

import console.BotReader;    
import console.BotWriter;      
import model.Review;          
import services.ReviewService; 
import java.util.List;        

public class ReviewHandler implements CommandHandler { 

    private final BotReader reader;     
    private final BotWriter writer;    
    private final ReviewService reviewService; 

    public ReviewHandler(BotReader reader, BotWriter writer, ReviewService reviewService) {
        this.reader = reader;         
        this.writer = writer;        
        this.reviewService = reviewService; 
    }

    @Override
    public String getCommandName() {
        return "Отзывы"; 
    }


    @Override
    public void execute() {
        boolean back = false; 

        while (!back) { 
            writer.write("\n--- Отзывы о книгах ---");
            writer.write("1. Оставить отзыв");
            writer.write("2. Читать все отзывы");
            writer.write("3. Найти отзывы по книге");
            writer.write("0. Назад");
            writer.write("Твой выбор: ");

            String choice = reader.readInput().trim(); 

            switch (choice) { 
                case "1":   
                    addReview();
                    break;
                case "2":    
                    readAllReviews();
                    break;
                case "3":   
                    searchReviews();
                    break;
                case "0":   
                    back = true; 
                    break;
                default:  
                    writer.write("Неверная команда.");
            }
        }
    }

    private void addReview() {
        writer.write("\nВведите название книги:");
        String bookTitle = reader.readInput().trim(); 

        writer.write("Введите ваше имя:"); 
        String author = reader.readInput().trim();  

        writer.write("Введите текст отзыва:"); 
        String text = reader.readInput().trim();

        Review review = new Review(bookTitle, author, text);
        reviewService.addReview(review);

        writer.write("✅ Отзыв добавлен!"); 
    }

    private void readAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();

        if (reviews.isEmpty()) { 
            writer.write("Отзывов пока нет."); 
            return; 
        }

        writer.write("\n--- Все отзывы ---"); 
        for (int i = 0; i < reviews.size(); i++) { 
            writer.write((i + 1) + ". " + reviews.get(i));
            writer.write("-------------------"); 
        }
    }

    private void searchReviews() {
        writer.write("\nВведите название книги для поиска:");
        String bookTitle = reader.readInput().trim();

        List<Review> reviews = reviewService.getReviewsByBook(bookTitle);

        if (reviews.isEmpty()) { 
            writer.write("Отзывов на эту книгу не найдено."); 
        } else {
            writer.write("\n--- Отзывы о \"" + bookTitle + "\" ---"); 
            for (int i = 0; i < reviews.size(); i++) { 
                writer.write((i + 1) + ". " + reviews.get(i)); 
                writer.write("-------------------");
            }
        }
    }
}