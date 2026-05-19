package com.sfl.actions.service;

import com.sfl.actions.model.UserModel;
import com.sfl.actions.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        UserModel input = new UserModel("joao", "joao@example.com");
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserModel saved = userService.createUser(input);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("joao", saved.getUsername());
        verify(userRepository, times(1)).save(input);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        UserModel a = new UserModel("a", "a@e.com");
        a.setId(1L);
        UserModel b = new UserModel("b", "b@e.com");
        b.setId(2L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(a, b));

        List<UserModel> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_found() {
        UserModel a = new UserModel("a", "a@e.com");
        a.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(a));

        Optional<UserModel> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("a", result.get().getUsername());
    }

    @Test
    void updateUser_existing() {
        UserModel existing = new UserModel("old", "old@e.com");
        existing.setId(1L);
        UserModel updated = new UserModel("new", "new@e.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(UserModel.class))).thenAnswer(i -> i.getArgument(0));

        Optional<UserModel> res = userService.updateUser(1L, updated);

        assertTrue(res.isPresent());
        assertEquals("new", res.get().getUsername());
        assertEquals("new@e.com", res.get().getEmail());
        verify(userRepository).save(existing);
    }

    @Test
    void deleteUser_shouldCallRepository() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}

