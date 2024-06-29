package store.ggun.admin.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {
    private String id;
    private String filename;
    private String contentType;
    private byte[] data;
}
