package com.clickjob.platform.controller;

import com.clickjob.platform.dto.request.RegisterRequest;
import com.clickjob.platform.dto.response.UserResponse;
import com.clickjob.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user-controller", description = "Gerenciamento de usuários (Freelancers e Empregadores)")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar um novo usuário", description = "Cria um novo usuário do tipo FREELANCER ou EMPLOYER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou e-mail já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Listar todos os freelancers", description = "Retorna uma lista de todos os usuários cadastrados com o perfil de FREELANCER.")
    @ApiResponse(responseCode = "200", description = "Lista de freelancers recuperada com sucesso")
    @GetMapping("/freelancers")
    public ResponseEntity<List<UserResponse>> getAllFreelancers() {
        return ResponseEntity.ok(userService.findAllFreelancers());
    }

    @Operation(summary = "Deletar um usuário", description = "Remove um usuário do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}