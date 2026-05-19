package com.sfl.actions.controller;

import com.sfl.actions.model.UserModel;
import com.sfl.actions.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void getUsers_shouldReturnList() {
        UserModel a = new UserModel("a", "a@e.com");
        a.setId(1L);
        UserModel b = new UserModel("b", "b@e.com");
        b.setId(2L);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(a, b));

        List<UserModel> result = userController.getUsers();

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getUsername());
    }

    @Test
    void createUser_shouldReturnCreatedResponse() {
        UserModel input = new UserModel("joao", "joao@e.com");
        UserModel saved = new UserModel("joao", "joao@e.com");
        saved.setId(1L);

        when(userService.createUser(input)).thenReturn(saved);

        ResponseEntity<UserModel> resp = userController.createUser(input);

        assertEquals(201, resp.getStatusCode().value());
        assertEquals(1L, resp.getBody().getId());
    }

    @Test
    void getUser_notFound_shouldReturn404() {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        ResponseEntity<UserModel> resp = userController.getUser(99L);

        assertEquals(404, resp.getStatusCode().value());
    }
}

