package com.clickjob.platform.controller;

import com.clickjob.platform.domain.enums.ApplicationStatus;
import com.clickjob.platform.dto.request.ApplicationRequest;
import com.clickjob.platform.dto.response.ApplicationResponse;
import com.clickjob.platform.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "application-controller", description = "Gerenciamento de candidaturas de freelancers às vagas")
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(summary = "Candidatar-se a uma vaga", description = "Cria uma candidatura de um freelancer para uma vaga específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Candidatura realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Freelancer já candidatado a esta vaga ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Vaga ou Freelancer não encontrados")
    })
    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request,
                                                     @RequestParam Long freelancerId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applicationService.apply(request.getJobId(), freelancerId));
    }

    @Operation(summary = "Listar candidaturas por vaga", description = "Retorna todas as candidaturas recebidas para uma vaga específica.")
    @ApiResponse(responseCode = "200", description = "Lista de candidaturas recuperada com sucesso")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.findByJob(jobId));
    }

    @Operation(summary = "Listar candidaturas por freelancer", description = "Retorna todas as vagas às quais um freelancer específico se candidatou.")
    @ApiResponse(responseCode = "200", description = "Lista de candidaturas do freelancer recuperada com sucesso")
    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<ApplicationResponse>> getByFreelancer(@PathVariable Long freelancerId) {
        return ResponseEntity.ok(applicationService.findByFreelancer(freelancerId));
    }

    @Operation(summary = "Atualizar status da candidatura", description = "Atualiza o status de uma candidatura (ex: PENDING, ACCEPTED, REFUSED, CANCELLED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Candidatura não encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }

    @Operation(summary = "Deletar uma candidatura", description = "Remove uma candidatura do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Candidatura deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Candidatura não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}