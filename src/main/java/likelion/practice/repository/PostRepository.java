// PostRepository.java
package likelion.practice.repository;

import likelion.practice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContainingOrContentContaining(String title, String content); // 제목 또는 본문 내용으로 검색
}

