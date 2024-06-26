package store.ggun.admin.visitor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final Sinks.Many<String> visitorCountSink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<Long> incrementVisitorCount() {
        String key = getCurrentDateKey();
        return visitorRepository.incrementVisitorCount(key)
                .doOnNext(count -> visitorCountSink.tryEmitNext(count.toString()));
    }

    public Mono<String> getVisitorCount() {
        String key = getCurrentDateKey();
        return visitorRepository.getVisitorCount(key);
    }

    public Flux<String> getVisitorCountStream() {
        return visitorCountSink.asFlux();
    }

    public Mono<Map<String, Long>> getVisitorCountByLast7Days() {
        Map<String, Long> dayMap = new HashMap<>();
        return Flux.range(0, 7)
                .map(i -> LocalDate.now().minusDays(i))
                .flatMapSequential(date -> {
                    String dateKey = "visitorCount:" + date;
                    return visitorRepository.getVisitorCount(dateKey)
                            .map(count -> {
                                dayMap.put(date.toString(), Long.parseLong(count));
                                return dayMap;
                            });
                })
                .then(Mono.just(dayMap));
    }

    public Mono<Long> getVisitorCountByMonth(String year, String month) {
        return visitorRepository.getVisitorCountByMonth(year, month);
    }

    public Mono<Map<String, Long>> getVisitorCountYearByMonth(String year) {
        Map<String, Long> monthMap = new HashMap<>();
        return Flux.range(1, 12)
                .flatMapSequential(month -> {
                    String monthKey = String.format("%02d", month);
                    return visitorRepository.getVisitorCountByMonth(year, monthKey)
                            .map(count -> {
                                monthMap.put(monthKey, count);
                                return monthMap;
                            });
                })
                .then(Mono.just(monthMap));
    }

    public Mono<Long> getVisitorCountByDate(String date) {
        return visitorRepository.getVisitorCountByDate(date);
    }

    private String getCurrentDateKey() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "visitorCount:" + today.format(formatter);
    }
}
