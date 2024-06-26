package store.ggun.admin.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailServiceImpl emailServiceImpl;

    @PostMapping("/send")
    public Mono<ResponseEntity<String>> sendMail(@RequestParam("to") String to,
                                                 @RequestParam("subject") String subject,
                                                 @RequestParam("text") String text) {
        return emailServiceImpl.sendEmail(to, subject, text)
                .thenReturn(ResponseEntity.ok("Email sent successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send mail: " + e.getMessage())));
    }

    @PostMapping("/send-all")
    public Mono<ResponseEntity<String>> sendAllMail(@RequestBody List<String> recipients,
                                                     @RequestParam("subject") String subject,
                                                     @RequestParam("text") String text) {

        return Flux.fromIterable(recipients) //Flux.fromIterable()는 Iterable 객체를 사용하여 Flux 생성
                .flatMap(recipient -> emailServiceImpl.sendEmail(recipient, subject, text))
                .then(Mono.just(ResponseEntity.ok("Email sent successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send mail: " + e.getMessage())));
    }

}
