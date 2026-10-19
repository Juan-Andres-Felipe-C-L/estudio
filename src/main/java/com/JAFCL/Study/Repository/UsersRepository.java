package com.JAFCL.Study.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.JAFCL.Study.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);
    
}
