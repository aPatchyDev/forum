package io.github.apatchydev.user.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NullMarked
@Data
@AllArgsConstructor
@Table
public class Member {
    @Nullable @Id
    private Long id;

    private String username;
    private String password;
}
