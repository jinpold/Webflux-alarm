package store.ggun.admin.email;


import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface EmailService {
    Mono<Void> sendEmail(String to, String subject, String text);
//    Mono<ResponseEntity<String>> sendBulkMail(String subject, String text);
}
