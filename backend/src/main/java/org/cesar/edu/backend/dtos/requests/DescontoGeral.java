package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record DescontoGeral(
        @NotNull String categoria,
        @NotNull Double desconto
) {}
