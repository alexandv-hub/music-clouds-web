package com.musicclouds.user.controller;

import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public void registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        log.info("New user registration {}", userRegistrationRequest);
        userService.registerUser(userRegistrationRequest);
    }

    @GetMapping
    public List<EntityModel<User>> getUsers() {
        log.info("Starting getUsers()...");
        List<User> users = userService.getAllUsers();
        List<EntityModel<User>> resources = new ArrayList<>();
        for (User user : users) {
            EntityModel<User> resource = EntityModel.of(user);
            resource.add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());
            resources.add(resource);
        }
        return resources;
    }

    @GetMapping("/{id}")
    public EntityModel<User> getUser(@PathVariable Integer id) {
        log.info("Starting getUser({})...", id);
        User user = userService.getUser(id);
        EntityModel<User> resource = EntityModel.of(user);
        resource.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        return resource;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        log.info("Starting deleteUser({})...", id);
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        log.info("Starting updateUser({})...", id);
        return userService.updateUser(id, updatedUser)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

}
