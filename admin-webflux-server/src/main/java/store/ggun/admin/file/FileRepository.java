package store.ggun.admin.file;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ReactiveMongoRepository<FileModel, String> {
}
