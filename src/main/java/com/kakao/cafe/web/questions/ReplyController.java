package com.kakao.cafe.web.questions;

import com.kakao.cafe.domain.User;
import com.kakao.cafe.service.ReplyService;
import com.kakao.cafe.web.SessionConst;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions/{questionId}/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping
    public String createReply(@RequestParam String contents, @PathVariable Long questionId, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.SESSIONED_USER);
        replyService.addReply(contents, questionId, user);
        return "redirect:/questions/" + questionId;
    }

    @DeleteMapping("/{id}")
    public String deleteReply(@PathVariable Long questionId, @PathVariable Long id, HttpSession session) {
        User sessionedUser = (User) session.getAttribute(SessionConst.SESSIONED_USER);
        replyService.deleteReply(id, sessionedUser); // 댓글 번호와 로그인 사용자 넘기기
        return "redirect:/questions/" + questionId;
    }
}
