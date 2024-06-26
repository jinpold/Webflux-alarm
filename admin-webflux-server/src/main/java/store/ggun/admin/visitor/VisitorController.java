package store.ggun.admin.visitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/visit")
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/OneVisit") // 방문자 수 더하기
    public ResponseEntity<Mono<Long>> visit() {
        return ResponseEntity.ok(visitorService.incrementVisitorCount());
    }

    @GetMapping(value = "/visitors", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // 방문자 수 스트림 조회
    public ResponseEntity<Flux<String>> getVisitorCountStream() {
        return ResponseEntity.ok(visitorService.getVisitorCountStream());
    }

    @GetMapping("/day") // 일별 방문자수 조회
    public ResponseEntity<Mono<Long>> getVisitorCountByDate(@RequestParam("date") String date) {
        return ResponseEntity.ok(visitorService.getVisitorCountByDate(date));
    }

    @GetMapping("/month") // 특정 월의 방문자 수 조회
    public ResponseEntity<Mono<Long>> getVisitorCountByMonth(@RequestParam("year") String year, @RequestParam("month") String month) {
        return ResponseEntity.ok(visitorService.getVisitorCountByMonth(year, month));
    }

    @GetMapping("/year") // 연간
    public ResponseEntity<Mono<Map<String, Long>>> getVisitorCountYearByMonth(@RequestParam("year") String year) {
        return ResponseEntity.ok(visitorService.getVisitorCountYearByMonth(year));
    }

    @GetMapping("/lastWeek") // 최근 일주일
    public ResponseEntity<Mono<Map<String, Long>>> getVisitorCountByLast7Days() {
        return ResponseEntity.ok(visitorService.getVisitorCountByLast7Days());
    }
}
