package store.ggun.admin.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public Mono<FileModel> save(FileModel fileModel) {
        return fileRepository.save(fileModel);
    }

    public Mono<FileModel> getFile(String id) {
        return fileRepository.findById(id);
    }
}
