package com.cepheid.cloud.skel;

import com.cepheid.cloud.skel.controller.BookController;
import com.cepheid.cloud.skel.model.Book;
import com.cepheid.cloud.skel.model.Reader;
import com.cepheid.cloud.skel.model.Review;
import com.cepheid.cloud.skel.repository.BookRepository;
import com.cepheid.cloud.skel.repository.ReaderRepository;
import com.cepheid.cloud.skel.repository.ReviewRepository;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackageClasses = {BookController.class, SkelApplication.class})
@EnableJpaRepositories(basePackageClasses = {ReaderRepository.class,BookRepository.class, ReviewRepository.class})
public class SkelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkelApplication.class, args);
    }

    @Bean
    ApplicationRunner initItems(BookRepository repository,ReaderRepository readerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            populateBookDB(repository);
            createUserAndAdmin(readerRepository, passwordEncoder);
        };
    }

    private void createUserAndAdmin(ReaderRepository readerRepository, PasswordEncoder passwordEncoder) {
        Reader user = Reader.builder()
            .username("user")
            .password(passwordEncoder.encode("user"))
            .role("USER")
            .build();
        Reader admin = Reader.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .role("ADMIN")
            .build();

        readerRepository.save(user);
        readerRepository.save(admin);
    }

    private void populateBookDB(BookRepository repository) {
        String[] titles = new String[]{
            "The old man and the sea",
            "Hobbit", "Lord of the rings",
            "Hobbit",
            "Silmarillion",
            "Unfinished Tales and The History of Middle-earth"};
        String[] authors = new String[]{
            "Ernest Hemingway",
            "J.R.R Tolkien",
            "J.R.R Tolkien",
            "J.R.R Tolkien",
            "J.R.R Tolkien",
            "J.R.R Tolkien"};
        String uuid = "543583b2-f9a0-4d26-ae2d-3ff9f4c335a";
        var ref = new Object() {
            int i = 0;
        };
        Arrays.stream(titles)
            .forEach(title -> {
                Book book = Book.builder()
                    .title(title)
                    .author(authors[ref.i])
                    .ISBN("978-3-16-148410-" + ref.i)
                    .uuid(UUID.fromString(uuid + ref.i))
                    .build();
                Review review = Review.builder()
                    .reviewer("Fazli Zekiqi")
                    .description("Some Description")
                    .build();
                book.addReview(review);
                repository.save(book);
                ref.i++;
            });
    }

}
