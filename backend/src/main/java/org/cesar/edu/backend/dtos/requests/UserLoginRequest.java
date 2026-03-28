package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
   @NotBlank(message = "O email é obrigatório") @NotNull String email,
   @NotBlank(message = "A senha é obrigatória") @NotNull String senha
) {}
