package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.loading.internal.CollectionLoadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service //서비스 선언! (서비스 객체를 스프링 부트에 선언)
public class ArticleService {

    @Autowired //DI
    private ArticleRepository articleRepository;

    public List<Article> index() {

        return articleRepository.findAll();
    }

    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(ArticleForm dto) {

        Article article = dto.toEntity();
        if(article.getId()!=null){
            return null;
        }
        return articleRepository.save(article);
    }

    public Article update(Long id, ArticleForm dto) {
        // 1. 수정용 엔티티 생성
        Article article = dto.toEntity();
        log.info("id: {}, article: {}", id, article.toString());

        // 2. 대상 엔티티 찾기
        Article target = articleRepository.findById(id).orElse(null);

        // 3. 잘못된 요청 처리 (대상이 없거나 , id가 다른 경우)
        if(target ==null || id!= article.getId()){
            log.info("잘못된 요청! id: {}, article: {}", id, article.toString());
            return null;
        }

        //4. 업데이트
        target.patch(article);
        Article updated = articleRepository.save(target);
        return updated;
    }


    public Article delete(Long id) {

        //1: 대상 찾기
        Article target = articleRepository.findById(id).orElse(null);

        //2: 잘못된 요청 처리 (대상이 없는 경우)
        if(target==null){
            return null;
        }
        //3:  대상 삭제 후 응답 반환
        articleRepository.delete(target);
        //데이터 반환
        return target;
    }

    @Transactional //해당 메소드를 트랜잭션으로 묶는다 -> 실패하면 이전상태로 롤백
    public List<Article> craeteArticles(List<ArticleForm> dtos) {

        //dto 묶음을 entity 묶음으로 변환
        List<Article> articleList = dtos.stream()
                .map(dto->dto.toEntity())
                .collect(Collectors.toList());

        //entity 묶음을 DB로 저장
        articleList.stream()
                .forEach(article -> articleRepository.save(article));
        //강제 예외 발생
        articleRepository.findById(-1L).orElseThrow(
                ()->new IllegalArgumentException("검색 실패")
        );

        //결과값 반환
        return null;

    }
}