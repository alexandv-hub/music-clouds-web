package com.musicclouds.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(
        name = "_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "user_username_unique",
                        columnNames = "username"
                )
        }
)
public class User {
    @Id
    @SequenceGenerator(
            name = "user_id_sequence",
            sequenceName = "_user_id_seq", // because postgres creates these automatically as "_user_id_seq"
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_sequence"
    )
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;


    public User(
            String firstName,
            String lastName,
            String email,
            String username,
            Integer age,
            Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.age = age;
        this.gender = gender;
    }

}
