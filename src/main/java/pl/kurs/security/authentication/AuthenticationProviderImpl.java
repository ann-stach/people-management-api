package pl.kurs.security.authentication;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kurs.security.model.LoginAttempt;
import pl.kurs.security.model.User;
import pl.kurs.security.repository.LoginAttemptRepository;
import pl.kurs.security.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private static final int LOCK_TIME_IN_MINUTES = 10;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final LoginAttemptRepository loginAttemptRepository;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        UserDetails user = userDetailsServiceImpl.loadUserByUsername(username);
        User actualUser = (User) user;

        if (!actualUser.isAccountNonLocked(LOCK_TIME_IN_MINUTES)) {
            throw new BadCredentialsException("Your account is locked. Please try again later.");
        }


        if (!passwordEncoder.matches(password, actualUser.getPassword())) {
            LoginAttempt loginAttempt = new LoginAttempt();
            loginAttempt.setLoginTime(LocalDateTime.now());
            loginAttempt.setSuccessful(false);
            loginAttempt.setUser(actualUser);
            loginAttemptRepository.saveAndFlush(loginAttempt);

            long failedAttempts = loginAttemptRepository.countFailedAttemptsInLast10Minutes(actualUser.getId(), LocalDateTime.now().minusMinutes(LOCK_TIME_IN_MINUTES));
            System.out.println("failed attempts in last 10min: " + failedAttempts);

            if (failedAttempts >= 3) {
                actualUser.setAccountLocked(true);
                actualUser.setLockTime(LocalDateTime.now());
              userRepository.saveAndFlush(actualUser);
            }
            throw new BadCredentialsException("Invalid password");
        }

        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setLoginTime(LocalDateTime.now());
        loginAttempt.setSuccessful(true);
        loginAttempt.setUser(actualUser);
        loginAttemptRepository.saveAndFlush(loginAttempt);
        userRepository.saveAndFlush(actualUser);

        return new UsernamePasswordAuthenticationToken(user, password, getAuthority(actualUser));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return authorities;
    }

}
