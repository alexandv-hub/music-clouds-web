package com.musicclouds.fraud.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "fraud_check_history")
public class FraudCheckHistory {
    @Id
    @SequenceGenerator(
            name = "fraud_id_sequence",
            sequenceName = "fraud_check_history_id_seq", // because postgres creates these automatically as "tablename_columnname_id_seq"
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fraud_id_sequence"
    )
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "is_fraudster")
    private Boolean isFraudster;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
