package com.sfl.actions.service;

import com.sfl.actions.model.UserModel;
import com.sfl.actions.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserModel createUser(UserModel user) {
        return userRepository.save(user);
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserModel> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserModel> updateUser(Long id, UserModel updated) {
        return userRepository.findById(id).map(existing -> {
            existing.setUsername(updated.getUsername());
            existing.setEmail(updated.getEmail());
            return userRepository.save(existing);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}

