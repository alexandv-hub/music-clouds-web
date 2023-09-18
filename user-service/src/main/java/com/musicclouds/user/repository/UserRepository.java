package com.musicclouds.user.repository;
import com.musicclouds.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsUserByEmail(String email);
    boolean existsUserById(Integer id);

}
