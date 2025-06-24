package org.wiwokdetok.kapsulkeaslian.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "password")
@Table(name = "users")
@Entity
public class User {

    @Id
    private UUID id;

    private String email;

    private String password;

    private String name;

    private String role;

    @Column(name = "profile_picture")
    private String profilePicture;

    private int followers;

    private int followings;

    private int points;
}
