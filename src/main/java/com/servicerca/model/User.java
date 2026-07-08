package com.servicerca.model;
import jakarta.persistence.*;import lombok.*;import java.math.BigDecimal;import java.time.Instant;import java.util.*;
@Entity @Table(name="app_user") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false,unique=true,length=254) private String email;
 @Column(name="password_hash") private String passwordHash;
 @Column(name="full_name",length=160) private String fullName;
 @Column(name="dni_encrypted",columnDefinition="text") private String dniEncrypted;
 @Column(length=40) private String phone;
 @Column(name="photo_url",columnDefinition="text") private String photoUrl;
 @Column(length=120) private String locality; private Double latitude; private Double longitude;
 @Column(name="onboarding_complete",nullable=false) private boolean onboardingComplete;
 @Column(name="professional_enabled",nullable=false) private boolean professionalEnabled;
 @Column(nullable=false) @Builder.Default private boolean available=true;
 @Column(nullable=false) @Builder.Default private BigDecimal rating=BigDecimal.ZERO;
 @Column(name="rating_count",nullable=false) private int ratingCount;
 @Column(name="deleted_at") private Instant deletedAt;
 @Column(name="policies_version",length=30) private String policiesVersion;
 @Column(name="policies_accepted_at") private Instant policiesAcceptedAt;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @ManyToMany(fetch=FetchType.EAGER) @JoinTable(name="user_trade",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="trade_id")) @Builder.Default private Set<Trade> trades=new HashSet<>();
 @PrePersist void prePersist(){var now=Instant.now();if(createdAt==null)createdAt=now;updatedAt=now;}
 @PreUpdate void preUpdate(){updatedAt=Instant.now();}
}
