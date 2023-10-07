package com.musicclouds.security.token;

import com.musicclouds.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "user")
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_id_seq")
  @SequenceGenerator(name = "token_id_seq", sequenceName = "token_id_seq", allocationSize = 1)
  public Integer id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;

  public boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
}
