package store.ggun.admin.post.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.notification.service.NotificationService;
import store.ggun.admin.post.domain.PostModel;
import store.ggun.admin.post.service.PostService;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "Customer not found")})
@RequestMapping(path = "/posts")
public class PostController {

    private final PostService postService;
    private final NotificationService notificationService;

    public PostController(PostService postService, NotificationService notificationService) {
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @PostMapping("/createPost")
    public Mono<PostModel> createPost(@RequestBody PostModel postModel) {
        return postService.createPost(postModel)
                .flatMap(savedPost -> {
                    NotificationModel notification = NotificationModel.builder()
                            .message("새 공지사항이 등록되었습니다: " + savedPost.getTitle())
                            .adminId("admin") // 알림을 받을 임직원 사용자의 ID
                            .response("admin response")
                            .hrAdminId("hrAdmin") // 인사 관리자 ID
                            .status("공지")
                            .build();
                    return notificationService.sendNotification(notification).thenReturn(savedPost);
                });
    }

    @GetMapping("/list")
    public Flux<PostModel> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/find/{id}")
    public Mono<PostModel> getPostById(@PathVariable String id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deletePostById(@PathVariable String id) {
        return postService.deletePostById(id);
    }
}
