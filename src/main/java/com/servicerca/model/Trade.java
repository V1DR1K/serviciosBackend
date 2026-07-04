package com.servicerca.model;
import jakarta.persistence.*;import lombok.*;
@Entity @Table(name="trade") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor public class Trade { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,unique=true,length=60) private String code; @Column(nullable=false,length=100) private String name; @Column(nullable=false,length=60) private String icon; }
