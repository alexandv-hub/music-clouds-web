package com.musicclouds.user.controller;

import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.dto.UserUpdateRequest;
import com.musicclouds.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class UserController {

    @Value("${frontend-react.user-service-url}")
    private String API_GATEWAY_URL;

    private final UserService userService;

    @PostMapping("/register")
    public void registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        log.info("New user registration {}", userRegistrationRequest);
        userService.registerUser(userRegistrationRequest);
        log.info("Successfully registered new user with email {}", userRegistrationRequest.email());
    }

    @GetMapping
    public List<EntityModel<User>> getUsers() {
        log.info("Starting getUsers()...");
        List<User> users = userService.getAllUsers();
        List<EntityModel<User>> resources = new ArrayList<>();
        for (User user : users) {
            EntityModel<User> resource = EntityModel.of(user);
            resource.add(linkTo(methodOn(UserController.class).getUser(user.getId()))
                    .withSelfRel()
                    .withHref(API_GATEWAY_URL + "api/v1/users/" + user.getId()));
            resources.add(resource);
        }
        return resources;
    }

    @GetMapping("/{id}")
    public EntityModel<User> getUser(@PathVariable Integer id) {
        log.info("Starting getUser({})...", id);
        User user = userService.getUser(id);
        EntityModel<User> resource = EntityModel.of(user);
        resource.add(linkTo(methodOn(UserController.class).getUser(id))
                .withSelfRel()
                .withHref(API_GATEWAY_URL + "api/v1/users/" + id));
        return resource;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserObjectById(@PathVariable Integer id) {
        log.info("Starting getUserObjectById({})...", id);
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        log.info("Starting deleteUser({})...", id);
        userService.deleteUserById(id);
        log.info("Successfully deleted user with id {}", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Starting updateUser({})...", id);
        return userService.updateUser(id, userUpdateRequest)
                .map(user -> {
                    log.info("Successfully updated user with id {}", id);
                    return ResponseEntity.ok().body(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
