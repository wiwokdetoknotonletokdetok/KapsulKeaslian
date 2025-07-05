package org.gaung.wiwokdetok.kapsulkeaslian.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String email;

    private String password;

    private String name;

    private String bio;

    private String role;

    @Column(name = "profile_picture")
    private String profilePicture;

    private int followers;

    private int followings;

    private int points;
}
