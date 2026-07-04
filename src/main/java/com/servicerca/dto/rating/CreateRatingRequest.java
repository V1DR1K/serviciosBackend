package com.servicerca.dto.rating; import jakarta.validation.constraints.*; public record CreateRatingRequest(@Min(1)@Max(5)short score,@Size(max=600)String comment){}
