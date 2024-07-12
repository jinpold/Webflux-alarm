package store.ggun.admin.post.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class PostModel {
    @Id
    private String id;
    private String title;
    private String content;
    private String authorId;
}
