package store.ggun.admin.email;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends ReactiveMongoRepository<EmailModel, String> {
}