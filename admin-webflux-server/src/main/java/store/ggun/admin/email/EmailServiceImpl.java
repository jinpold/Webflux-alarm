package store.ggun.admin.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Override
    public Mono<Void> sendEmail(String to, String subject, String text) {
        return Mono.fromRunnable(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, true);
                mailSender.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }).then(saveEmail(to, subject, text));
    }

    @Override
    public Mono<Void> saveEmail(String to, String subject, String text) {
        EmailModel emailModel = EmailModel.builder()
                .email(to)
                .subject(subject)
                .message(text)
                .build();
        return emailRepository.save(emailModel).then();
    }

    @Override
    public Mono<ResponseEntity<String>> sendAllMail(List<String> recipients, String subject, String text) {
        return Flux.fromIterable(recipients)
                .flatMap(recipient -> sendEmail(recipient, subject, text))
                .then(Mono.just(ResponseEntity.ok("Emails sent successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send emails: " + e.getMessage())));
    }
}
