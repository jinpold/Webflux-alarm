package store.ggun.admin.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/redis") // 추가된 부분: API 경로를 명확하게 하기 위해 추가했습니다.
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/set")
    public ResponseEntity<Map<String, String>> setKeyValue() {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        Map<String, String> response = new HashMap<>();
        vop.set("Korea", "Seoul");
        response.put("Korea", "Seoul");
        vop.set("America", "NewYork");
        response.put("America", "NewYork");
        vop.set("Italy", "Rome");
        response.put("Italy", "Rome");
        vop.set("Japan", "Tokyo");
        response.put("Japan", "Tokyo");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, String>> getValueFromKey(@PathVariable String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String value = vop.get(key);
        Map<String, String> response = new HashMap<>();
        if (value != null) {
            response.put(key, value);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Key not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
