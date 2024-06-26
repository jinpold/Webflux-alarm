package store.ggun.admin.visitor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class VisitorRepository {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Long> incrementVisitorCount(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Mono<String> getVisitorCount(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Long> getVisitorCountByDate(String date) {
        return redisTemplate.opsForValue().get("visitorCount:" + date)
                .map(Long::parseLong);
    }

    public Mono<Long> getVisitorCountByMonth(String year, String month) {
        return redisTemplate.keys("visitorCount:" + year + "-" + month + "-*")
                .flatMap(redisTemplate.opsForValue()::get)
                .map(Long::parseLong)
                .reduce(0L, Long::sum);
    }

    public Mono<Map<String, Long>> getVisitorCountYearByMonth(String year) {
        Map<String, Long> monthMap = new HashMap<>();
        return Flux.range(1, 12)
                .flatMapSequential(month -> {
                    String monthKey = String.format("%02d", month);
                    return getVisitorCountByMonth(year, monthKey)
                            .doOnNext(count -> monthMap.put(monthKey, count));
                })
                .then(Mono.just(monthMap));
    }

    public Mono<Map<String, Long>> getVisitorCountByLast7Days() {
        Map<String, Long> dayMap = new HashMap<>();
        return Flux.range(0, 7)
                .flatMapSequential(i -> {
                    LocalDate date = LocalDate.now().minusDays(i);
                    String dateKey = "visitorCount:" + date;
                    return getVisitorCountByDate(dateKey)
                            .defaultIfEmpty(0L)
                            .doOnNext(count -> dayMap.put(date.toString(), count));
                })
                .then(Mono.just(dayMap));
    }
}
