package com.servicerca.dto.auth; import jakarta.validation.constraints.*; public record RegisterRequest(@Email @NotBlank String email,@Size(min=8,max=72) String password,boolean acceptedPolicies){}
