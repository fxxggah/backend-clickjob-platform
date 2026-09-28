package com.clickjob.platform.controller;

import com.clickjob.platform.domain.enums.JobStatus;
import com.clickjob.platform.dto.request.JobRequest;
import com.clickjob.platform.dto.response.JobResponse;
import com.clickjob.platform.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "job-controller", description = "Gerenciamento de vagas (Jobs)")
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "Criar uma nova vaga", description = "Cria uma nova vaga associada a um empregador específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Empregador não encontrado")
    })
    @PostMapping
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest request, @RequestParam Long employerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(request, employerId));
    }

    @Operation(summary = "Buscar vaga por ID", description = "Retorna os detalhes de uma vaga específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vaga encontrada"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findById(id));
    }

    @Operation(summary = "Listar vagas abertas", description = "Retorna uma lista de todas as vagas que estão com o status OPEN.")
    @ApiResponse(responseCode = "200", description = "Lista de vagas abertas recuperada com sucesso")
    @GetMapping("/open")
    public ResponseEntity<List<JobResponse>> getAllOpen() {
        return ResponseEntity.ok(jobService.findAllOpen());
    }

    @Operation(summary = "Listar vagas por empregador", description = "Retorna todas as vagas criadas por um empregador específico.")
    @ApiResponse(responseCode = "200", description = "Lista de vagas do empregador recuperada com sucesso")
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<List<JobResponse>> getByEmployer(@PathVariable Long employerId) {
        return ResponseEntity.ok(jobService.findByEmployer(employerId));
    }

    @Operation(summary = "Atualizar status da vaga", description = "Atualiza o status de uma vaga (ex: de OPEN para CLOSED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido fornecido"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<JobResponse> updateStatus(@PathVariable Long id,
                                                    @RequestParam JobStatus status) {
        return ResponseEntity.ok(jobService.updateStatus(id, status));
    }

    @Operation(summary = "Deletar uma vaga", description = "Remove uma vaga do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vaga deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}