package com.kakao.cafe.web.questions;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.domain.User;
import com.kakao.cafe.service.ArticleService;
import com.kakao.cafe.web.questions.dto.ArticleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class ArticleController {

    private final ArticleService articleService;
    private final Logger log = LoggerFactory.getLogger(ArticleController.class);

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public String requestForm(Model model) {
        log.info("create form 접근");
        model.addAttribute("article", new ArticleDto());
        return "/qna/form";
    }

    @PostMapping
    public String saveForm(@Validated @ModelAttribute("article") ArticleDto dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("saveForm Validation had Errors");
            return "/qna/form";
        }

        articleService.addArticle(dto);
        log.info("save form = {}", dto.getTitle());
        return "redirect:/";
    }

    @GetMapping("/{index}")
    public String getArticle(@PathVariable Long index, Model model) {
        Article findArticle = articleService.findArticleById(index);
        model.addAttribute("article", findArticle);
        log.info("get article title = {}", findArticle.getTitle());
        log.info("get article content = {}", findArticle.getContents());
        return "/qna/show";
    }

    @PostMapping("/{index}/delete")
    public String deleteArticle(@PathVariable Long index, HttpServletRequest request) {
        if (!isOwnArticle(index, request)) {
            log.info("delete article error : 자신의 글만 삭제 가능");
            return "redirect:/questions/" + index;
        }

        log.info("delete article id = {}", index);
        articleService.deleteArticle(index);
        return "redirect:/";
    }

    @GetMapping("/{index}/update")
    public String getArticleUpdateForm(@PathVariable Long index, HttpServletRequest request, Model model) {
        if (!isOwnArticle(index, request)) {
            log.info("update article error : 자신의 글만 수정 가능");
            return "redirect:/questions/" + index;
        }

        log.info("get profile update form");
        model.addAttribute("article", articleService.findArticleDtoById(index));
        return "/qna/updateForm";
    }

    @PostMapping("/{index}/update")
    public String saveArticleUpdateForm(@Validated @ModelAttribute("article") ArticleDto dto, @PathVariable Long index, HttpSession session) {
        articleService.updateArticle(index, dto, session.getAttribute("SESSIONED_USER"));
        log.info("save form = {}", dto.getTitle());
        return "redirect:/questions/" + index;
    }

    private boolean isOwnArticle(Long index, HttpServletRequest request) {
        Article findArticle = articleService.findArticleById(index);

        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("SESSIONED_USER");

        if (!sessionUser.isOwnArticle(findArticle)) {
            return false;
        }
        return true;
    }
}
