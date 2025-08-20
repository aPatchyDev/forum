package io.github.apatchydev.user.v1.repository;

import io.github.apatchydev.user.v1.model.Member;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberRepository extends ListCrudRepository<Member, Long> {
    Member findByUsername(String username);
}
