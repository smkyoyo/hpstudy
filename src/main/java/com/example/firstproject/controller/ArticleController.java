package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j // annotation for logging
public class ArticleController {

    //Spring boot가 알아서 객체를 생성해 주기 때문에 미리 생성해 놓은 객체를 자동으로 연결해줌
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm(){
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form){

        log.info(form.toString());
        //System.out.println(form); -> change to logging

        // 1. Dto를 변환! Entity!
        Article article = form.toEntity();
        log.info(article.toString());
        //System.out.println(article.toString());

        // 2. Repository에게 Entity를 DB안에 저장하게 함
        Article saved = articleRepository.save(article);
        log.info(article.toString());
        //System.out.println(article.toString());
        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable  Long id, Model model){
        log.info("id=" + id);

        // 1: load data
        Article articleEntity = articleRepository.findById(id).orElse(null);

        // 2: register data into model
        model.addAttribute("article", articleEntity);

        // 3:
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model){

        // 1. load all article data
        List<Article> articleEntityList = articleRepository.findAll();

        // 2. submit as a view for the bunch of articles
        model.addAttribute("articleList", articleEntityList);

        return "articles/index"; //articles/index.mustache
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        //load data to edit
        Article articleEntity = articleRepository.findById(id).orElse(null);

        //register data into model
        model.addAttribute("article", articleEntity);

        //view page setting
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form){
        log.info(form.toString());

        // 1. Dto를 엔티티로 변환
        Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());
        // 2. 엔티티를 DB로 저장
        //2-1: DB에서 기존 데이터를 가져온다.
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);

        //2-2: 기존 데이터 값을 갱신
        if (target !=null){
            articleRepository.save(articleEntity);
        }
        // 3. 수정 결과 페이지를 리다이렉트
        return "redirect:/articles/"+articleEntity.getId();
    }

    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("삭제요청이 들어왔습니다.");

        // 1. load target to delete
        Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());

        // 2. Delete the target
        if(target!=null){
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제가 완료되었습니다.");
        }

        // 3. redirect to result page
        return "redirect:/articles";
    }
}
