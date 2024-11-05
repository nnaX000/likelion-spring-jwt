// PostController.java
package likelion.practice.controller;

import likelion.practice.dto.PostDTO;
import likelion.practice.entity.Post;
import likelion.practice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시물 작성
    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        // DTO를 엔티티로 변환
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setImages(postDTO.getImages());

        // 서비스에서 엔티티 저장
        Post createdPost = postService.createPost(post);

        // 엔티티를 DTO로 변환하여 반환
        PostDTO createdPostDTO = new PostDTO();
        createdPostDTO.setTitle(createdPost.getTitle());
        createdPostDTO.setContent(createdPost.getContent());
        createdPostDTO.setImages(createdPost.getImages());

        return new ResponseEntity<>(createdPostDTO, HttpStatus.CREATED);
    }

    // 게시물 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // 게시물 검색 (제목 또는 본문으로 검색)
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchPosts(keyword);

        // 엔티티 리스트를 DTO 리스트로 변환
        List<PostDTO> postDTOs = posts.stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setImages(post.getImages());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(postDTOs);
    }
}
