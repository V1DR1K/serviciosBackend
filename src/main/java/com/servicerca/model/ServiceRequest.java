package com.servicerca.model;
import jakarta.persistence.*;import lombok.*;import java.math.BigDecimal;import java.time.Instant;import java.util.UUID;
@Entity @Table(name="service_request") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceRequest {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(optional=false) @JoinColumn(name="client_id") private User client;
 @ManyToOne(optional=false) @JoinColumn(name="trade_id") private Trade trade;
 @Column(nullable=false,length=140) private String title; @Column(nullable=false,length=1600) private String description;
 @Column(nullable=false,length=120) private String locality; @Column(nullable=false) private Double latitude; @Column(nullable=false) private Double longitude;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) @Builder.Default private Urgency urgency=Urgency.NORMAL;
 private BigDecimal budget; @Enumerated(EnumType.STRING) @Column(nullable=false,length=32) @Builder.Default private RequestStatus status=RequestStatus.DRAFT;
 @OneToOne @JoinColumn(name="selected_application_id") private ServiceApplication selectedApplication;
 @Column(name="client_completed",nullable=false) private boolean clientCompleted;
 @Column(name="professional_completed",nullable=false) private boolean professionalCompleted;
 @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt; @Column(name="deleted_at") private Instant deletedAt;
 @PrePersist void prePersist(){var now=Instant.now();if(createdAt==null)createdAt=now;updatedAt=now;} @PreUpdate void preUpdate(){updatedAt=Instant.now();}
}
