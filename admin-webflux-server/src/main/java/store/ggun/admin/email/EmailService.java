package store.ggun.admin.email;


import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmailService {
    Mono<Void> sendEmail(String to, String subject, String text);
    Mono<ResponseEntity<String>> sendAllMail(List<String> recipients, String subject, String text);
    Mono<Void> saveEmail(String to, String subject, String text);
}
