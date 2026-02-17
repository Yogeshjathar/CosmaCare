package com.cosmacare.cosmacare_user_service.repository;

import com.cosmacare.cosmacare_user_service.entity.Status;
import com.cosmacare.cosmacare_user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findById(UUID id);
    List<User> findByStatus(Status status);
    @Transactional
    void deleteByUserName(String name);

    boolean existsByUserName(String name);
}
