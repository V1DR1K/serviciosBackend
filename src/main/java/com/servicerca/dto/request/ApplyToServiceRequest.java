package com.servicerca.dto.request; import jakarta.validation.constraints.Size; public record ApplyToServiceRequest(@Size(max=500)String message){}
