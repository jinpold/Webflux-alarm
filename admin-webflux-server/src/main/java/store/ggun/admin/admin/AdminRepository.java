package store.ggun.admin.admin;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface AdminRepository extends ReactiveMongoRepository<AdminModel, String> {
    Mono<AdminModel> findByUsername(String username);
}
