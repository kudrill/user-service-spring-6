package com.example.user_service.controller;

import com.example.user_service.dto.UserDto;
import com.example.user_service.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAll() {
        List<EntityModel<UserDto>> users = service.getAll().stream()
                .map(dto -> EntityModel.of(dto,
                        linkTo(methodOn(UserController.class).getById(dto.id())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getAll()).withRel("users")))
                .collect(Collectors.toList());

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<UserDto> getById(@PathVariable Long id) {
        UserDto dto = service.getById(id);
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> create(@RequestBody UserDto dto) {
        UserDto created = service.create(dto);
        EntityModel<UserDto> model = EntityModel.of(created,
                linkTo(methodOn(UserController.class).getById(created.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"));

        URI location = URI.create("/api/users/" + created.id());
        return ResponseEntity.created(location).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        UserDto updated = service.update(id, dto);
        return EntityModel.of(updated,
                linkTo(methodOn(UserController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
