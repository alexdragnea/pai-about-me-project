package net.dg.paiproiect.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 2, max = 30, message = "First Name should be between 2 and 30 characters.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Size(min = 2, max = 30, message = "Last Name should be between 2 and 30 characters.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotEmpty(message = "Email should not be empty.")
    private String email;

    @NotEmpty(message = "Password should not be empty.")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "isNonLocked")
    @Builder.Default
    private boolean isNonLocked = true;


    @Override
    public List<UserRole> getAuthorities() {
        return new ArrayList<>(Collections.singleton(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNonLocked;
    }

    public void setAccountLocked() {
        isNonLocked = false;
    }

    public void setAccountUnLocked() {
        isNonLocked = true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}