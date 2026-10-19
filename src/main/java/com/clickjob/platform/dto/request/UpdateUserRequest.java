package com.clickjob.platform.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String name;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]*$", message = "Formato de telefone inválido")
    private String phone;

    @Size(max = 500, message = "A descrição 'sobre mim' não pode exceder 500 caracteres")
    private String aboutMe;
}