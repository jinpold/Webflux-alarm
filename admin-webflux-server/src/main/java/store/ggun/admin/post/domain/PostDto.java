package store.ggun.admin.post.domain;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PostDto {
    private String id;
    private String title;
    private String content;
    private String authorId;
}
