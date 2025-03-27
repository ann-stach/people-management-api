package pl.kurs.security.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "users")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String password;

    @ManyToMany
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<LoginAttempt> attempts = new HashSet<>();

    private boolean accountLocked = false;
    private LocalDateTime lockTime;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (accountLocked && lockTime != null && lockTime.plusMinutes(1).isAfter(LocalDateTime.now())) {
            return false;
        }
        this.accountLocked = false;
        this.lockTime = null;
        return true;
    }

    public boolean isAccountNonLocked(int lockTimeInMinutes) {
        if (accountLocked && lockTime != null && lockTime.plusMinutes(1).isAfter(LocalDateTime.now())) {
            return false;
        }
        this.accountLocked = false;
        this.lockTime = null;
        return true;
    }

    public boolean isLocked(int lockForMinutes) {
        if (accountLocked && lockTime != null && lockTime.plusMinutes(lockForMinutes).isAfter(LocalDateTime.now())) {
            return true;
        }
        accountLocked = false;
        return false;
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
