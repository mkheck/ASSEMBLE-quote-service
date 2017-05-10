package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

//@EnableBinding(Sink.class)
@EnableDiscoveryClient
@SpringBootApplication
public class QuoteServiceApplication {
    @Bean
    CommandLineRunner commandLineRunner(QuoteRepository quoteRepository) {
        return strings -> {
            quoteRepository.save(new Quote("The unexamined life is not worth living.", "Socrates"));
            quoteRepository.save(new Quote("What you do makes a difference, and you have to decide what kind of difference you want to make.", "Jane Goodall"));
            quoteRepository.save(new Quote("Do you want to know who you are? Don't ask. Act! Action will delineate and define you.", "Thomas Jefferson"));
            quoteRepository.save(new Quote("Love is the absence of judgment.", "Dalai Lama XIV"));
            quoteRepository.save(new Quote("You have power over your mind - not outside events. Realize this, and you will find strength.", "Marcus Aurelius, Meditations"));
            quoteRepository.save(new Quote("It's hard to beat a person who never gives up.", "Babe Ruth"));
            quoteRepository.save(new Quote("Imagination is the highest form of research.", "Albert Einstein"));

            quoteRepository.findAll().forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(QuoteServiceApplication.class, args);
    }
}

//@MessageEndpoint
//class MessageProcessor {
//    private final QuoteRepository quoteRepository;
//
//    public MessageProcessor(QuoteRepository quoteRepository) {
//        this.quoteRepository = quoteRepository;
//    }
//
//    @StreamListener(Sink.INPUT)
//    public void saveQuote(Quote quote) {
//        System.out.println(this.quoteRepository.save(quote));
//    }
//}

@RestController
class QuoteController {
    private final QuoteRepository quoteRepository;

    public QuoteController(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @GetMapping("/random")
    public Quote getRandomQuote() {
        return this.quoteRepository.getQuotesRandomOrder().get(0);
    }
}

@RepositoryRestResource
interface QuoteRepository extends CrudRepository<Quote, Long> {
    @Query("select q from Quote q order by RAND()")
    List<Quote> getQuotesRandomOrder();
}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Quote {
    @Id
    @GeneratedValue
    private Long id;
    private String text, source;

    public Quote(String text, String source) {
        this.text = text;
        this.source = source;
    }
}