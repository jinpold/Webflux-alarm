package store.ggun.admin.chat.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {
    private String id;
    private String roomId;
    private String senderId;
    private String senderName;
    private String message;
    private LocalDateTime createdAt;
}