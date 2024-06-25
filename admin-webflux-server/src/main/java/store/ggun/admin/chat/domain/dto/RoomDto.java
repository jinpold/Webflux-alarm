package store.ggun.admin.chat.domain.dto;
import java.util.List;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    private String id;
    private String title;
    private List<String> members;
}
