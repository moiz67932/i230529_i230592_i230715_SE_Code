package com.pipelinex.users;

import com.pipelinex.shared.domain.Role;
import com.pipelinex.shared.domain.UserStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByRole(Role role);

    long countByRoleAndStatus(Role role, UserStatus status);

    @Query("""
        select u from User u
        where (:query is null or lower(u.fullName) like lower(concat('%', :query, '%'))
            or lower(u.email) like lower(concat('%', :query, '%')))
          and (:status is null or u.status = :status)
          and u.role = com.pipelinex.shared.domain.Role.REP
        """)
    List<User> searchReps(String query, UserStatus status, Sort sort);

    List<User> findByRoleAndStatus(Role role, UserStatus status, Sort sort);
}
