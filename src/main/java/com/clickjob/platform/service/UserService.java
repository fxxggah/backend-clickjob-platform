package com.clickjob.platform.service;

import com.clickjob.platform.domain.model.User;
import com.clickjob.platform.domain.repository.UserRepository;
import com.clickjob.platform.dto.request.RegisterRequest;
import com.clickjob.platform.dto.request.UpdateUserRequest;
import com.clickjob.platform.dto.response.UserResponse;
import com.clickjob.platform.exception.ResourceAlreadyExistsException;
import com.clickjob.platform.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Usuário com o Email: " + request.getEmail() + " já cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType())
                .build();

        user = userRepository.save(user);

        return toResponse(user);
    }

    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com o ID: " + id + " não encontrado"));
    }

    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com o Email: " + email + " não encontrado"));
    }

    public UserResponse findById(Long id) {
        return toResponse(getEntityById(id));
    }

    public List<UserResponse> findAllFreelancers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getUserType().name().equals("FREELANCER"))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .aboutMe(user.getAboutMe())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        User user = getEntityById(id);
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse updateProfile(Long id, UpdateUserRequest request) {
        User user = getEntityById(id);
        
        user.setPhone(request.getPhone());
        user.setAboutMe(request.getAboutMe());

        user = userRepository.save(user);

        return toResponse(user);
    }

}