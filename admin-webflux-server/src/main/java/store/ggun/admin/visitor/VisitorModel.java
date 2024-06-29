package store.ggun.admin.visitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("VisitorCount")
public class VisitorModel {
    @Id
    private String date;
    private Long count;
    private String month;
    private String year;
}

