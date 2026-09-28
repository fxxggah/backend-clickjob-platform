package com.clickjob.platform.service;

import com.clickjob.platform.domain.model.User;
import com.clickjob.platform.domain.repository.UserRepository;
import com.clickjob.platform.dto.request.RegisterRequest;
import com.clickjob.platform.dto.response.UserResponse;
import com.clickjob.platform.exception.ResourceAlreadyExistsException;
import com.clickjob.platform.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.clickjob.platform.domain.enums.UserType.EMPLOYER;
import static com.clickjob.platform.domain.enums.UserType.FREELANCER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    // CRUD Testes

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void deveRegistrarUsuario() {

        RegisterRequest user = new RegisterRequest("Gabriel", "gabriel@gmail.com", "gabriel", EMPLOYER);

        User savedUser = User.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .password("encodedPassword")
                .userType(user.getUserType())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        UserResponse result = userService.register(user);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserType(), result.getUserType());

        verify(userRepository).save(any(User.class));
        verify(userRepository).existsByEmail(user.getEmail());
        verify(passwordEncoder).encode(user.getPassword());
    }

    @Test
    @DisplayName("Deve retornar usuário pelo ID")
    void deveRetornarUsuarioPeloId() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getEntityById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserType(), result.getUserType());

        verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("Deve retornar usuário pelo Email")
    void deveRetornarUsuarioPeloEmail() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = userService.getEntityByEmail(user.getEmail());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserType(), result.getUserType());

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Deve retornar usuário convertido em DTO pelo ID")
    void deveRetornarUsuariomDTOPeloId() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserType(), result.getUserType());

        verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("Deve retornar todos usuários do tipo freelancer")
    void deveRetornarTodosOsFreelancers() {

        User userOne = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(FREELANCER)
                .build();

        User userTwo = User.builder()
                .id(2L)
                .name("Hellen")
                .email("hellencarol@gmail.com")
                .password("hellencarol")
                .userType(FREELANCER)
                .build();

        User userThree = User.builder()
                .id(3L)
                .name("Silvana")
                .email("silvana@gmail.com")
                .password("silvana")
                .userType(EMPLOYER)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(userOne, userTwo, userThree));

        List<UserResponse> result = userService.findAllFreelancers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Gabriel", result.get(0).getName());
        assertEquals("Hellen", result.get(1).getName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve deletar usuário pelo ID")
    void deveDeletarUsuarioPeloId() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(FREELANCER)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        verify(userRepository).findById(user.getId());
        verify(userRepository).delete(user);
    }

    // Exceptions Testes

    @Test
    @DisplayName("Deve lançar ResourceAlreadyExistsException quando o e-mail já estiver cadastrado")
    void deveLancarExcecaoQuandoEmailJaExistir() {

        RegisterRequest request = RegisterRequest.builder()
                .email("gabriel@gmail.com")
                .name("Gabriel")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.register(request);
        });

        assertEquals("Usuário com o Email: " + request.getEmail() + " já cadastrado", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar entidade por ID inexistente")
    void deveLancarExcecaoQuandoIDNaoExiste() {

        Long idInexistente = 1L;

        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getEntityById(idInexistente);
        });

        assertEquals("Usuário com o ID: " + idInexistente + " não encontrado", exception.getMessage());

        verify(userRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar entidade por e-mail inexistente")
    void deveLancarExcecaoQuandoEmailNaoExistir() {

        String emailInexistente = "gabriel@gmail.com";

        when(userRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getEntityByEmail(emailInexistente);
        });

        assertEquals("Usuário com o Email: " + emailInexistente + " não encontrado", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(emailInexistente);
    }
}