package store.ggun.admin.admin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminService {
    Mono<AdminModel> save(AdminDto adminDto);
    Mono<AdminModel> findById(String id);
    Flux<AdminModel> findAll();
    Mono<AdminModel> update(String id,AdminDto adminDto);
    Mono<Void> delete(String id);

    Mono<String> permit(String id);
    Mono<String> revoke(String id);
    Flux<AdminModel> findAllByEnabled();
    Mono<Long> countAdminsEnabledFalse();

    Flux<AdminDto> searchByName(String keyword);
}
