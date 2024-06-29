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

    @PostMapping("/Admin-send")
    public Mono<ResponseEntity<String>> sendMail(@RequestBody EmailDto emailDto) {
        return emailServiceImpl.sendEmail(emailDto.getEmail(), emailDto.getSubject(), emailDto.getMessage())
                .thenReturn(ResponseEntity.ok("Email sent successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send mail: " + e.getMessage())));
    }

    @PostMapping("/Admin-send-all")
    public Mono<ResponseEntity<String>> sendAllMail(@RequestBody EmailDto emailDto) {
        return emailServiceImpl.sendAllMail(emailDto.getRecipients(), emailDto.getSubject(), emailDto.getMessage());
    }
}
