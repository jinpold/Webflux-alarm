package store.ggun.admin.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class EmailDto {
    private String id;
    private String email; // 수신자 이메일
    private String subject; // 추가
    private String message;
    private List<String> recipients;
}
