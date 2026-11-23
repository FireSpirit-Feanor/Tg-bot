package services;

import model.Review;
import java.io.*; 
import java.util.ArrayList; 
import java.util.List;

public class ReviewService { 

    private static final String FILE_PATH = "reviews.json";
    private List<Review> reviews; 
    
    public ReviewService() {
        this.reviews = loadReviews();
    }

    public void addReview(Review review) {
        reviews.add(review);
        saveReviews(); 
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }

    public List<Review> getReviewsByBook(String bookTitle) {
        List<Review> result = new ArrayList<>();

        for (Review review : reviews) {
            if (review.getBookTitle().toLowerCase().contains(bookTitle.toLowerCase())) {
                result.add(review);
            }
        }

        return result; 
    }

    private void saveReviews() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {

            for (Review review : reviews) { 
                writer.println(review.getBookTitle() + "|" + 
                             review.getAuthor() + "|" + 
                             review.getText() + "|" + 
                             review.getDate());
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения отзывов: " + e.getMessage()); 
        }
    }

    private List<Review> loadReviews() {
        List<Review> loaded = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) { 
            return loaded; 
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {


            String line; 
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 4);

                if (parts.length == 4) { 
                 
                    Review review = new Review(parts[0], parts[1], parts[2]);
                    loaded.add(review);
                }
            }
        } catch (IOException e) { 
            System.err.println("Ошибка загрузки отзывов: " + e.getMessage()); 
        }

        return loaded;
    }
}