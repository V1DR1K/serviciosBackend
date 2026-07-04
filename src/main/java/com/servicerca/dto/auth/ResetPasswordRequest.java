package com.servicerca.dto.auth; import jakarta.validation.constraints.*; public record ResetPasswordRequest(@NotBlank String token,@Size(min=8,max=72) String password){}
