package com.servicerca.dto.media; import java.util.UUID; public record MediaUploadResponse(UUID id,String url,String contentType,long sizeBytes){}
