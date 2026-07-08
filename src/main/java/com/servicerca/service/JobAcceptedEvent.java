package com.servicerca.service;import java.util.UUID;public record JobAcceptedEvent(UUID requestId,String clientEmail,String clientName,String professionalName,String title,String locality){}
