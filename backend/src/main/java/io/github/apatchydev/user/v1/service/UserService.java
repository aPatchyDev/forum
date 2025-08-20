package io.github.apatchydev.user.v1.service;

import io.github.apatchydev.user.v1.model.Member;
import io.github.apatchydev.user.v1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@NullMarked
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final MemberRepository memberRepository;

    public URI createUser(String username, String password) {
        var user = new Member(null,  username, password);
        memberRepository.save(user);
        return URI.create("/v1/users/" + username);
    }

    public void deleteUser(String username, String password) {
        var user = memberRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Failed to delete user");
        }
        memberRepository.delete(user);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        var user = memberRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Failed to change password");
        }
        user.setPassword(newPassword);
        memberRepository.save(user);
    }
}
