package com.servicerca.dto.auth; import jakarta.validation.constraints.*; public record RecoveryRequest(@Email @NotBlank String email){}
