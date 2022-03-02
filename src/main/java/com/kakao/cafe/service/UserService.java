package com.kakao.cafe.service;

import com.kakao.cafe.domain.User;
import com.kakao.cafe.exception.DuplicateUserException;
import com.kakao.cafe.exception.UserNotFoundException;
import com.kakao.cafe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final String USER_NOT_FOUND_EXCEPTION = "해당 사용자를 찾을 수 없습니다.";
    private static final String ALREADY_REGISTER_USER_EXCEPTION = "이미 가입된 회원 입니다.";
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Long register(User user) {
        if (isDuplicateUserId(user.getUserId())) {
            throw new DuplicateUserException(ALREADY_REGISTER_USER_EXCEPTION);
        }
        repository.save(user);
        return user.getId();
    }

    private boolean isDuplicateUserId(String userId) {
        return repository.findAll().stream()
                .anyMatch(u -> userId.equals(u.getUserId()));
    }

    public List<User> findUsers() {
        return repository.findAll();
    }

    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));
    }
}