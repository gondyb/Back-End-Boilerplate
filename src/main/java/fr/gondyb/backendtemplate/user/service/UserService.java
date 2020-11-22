package fr.gondyb.backendtemplate.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import fr.gondyb.backendtemplate.security.JWTUserDetails;
import fr.gondyb.backendtemplate.security.SecurityProperties;
import fr.gondyb.backendtemplate.user.command.CreateUserCommand;
import fr.gondyb.backendtemplate.user.query.UserView;
import fr.gondyb.backendtemplate.user.query.UserViewRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final String ADMIN_AUTHORITY = "ADMIN";
    private static final String USER_AUTHORITY = "USER";
    private final CommandGateway commandGateway;
    private final SecurityProperties properties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final PasswordEncoder passwordEncoder;
    private final UserViewRepository repository;

    @Override
    @Transactional
    public JWTUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository
                .findByEmail(email)
                .map(user -> getUserDetails(user, getToken(user)))
                .orElseThrow(() -> new UsernameNotFoundException("Username or password didn''t match"));
    }

    @Transactional
    public JWTUserDetails loadUserByToken(String token) {
        return getDecodedToken(token)
                .map(DecodedJWT::getSubject)
                .flatMap(repository::findByEmail)
                .map(user -> getUserDetails(user, token))
                .orElseThrow(RuntimeException::new); // TODO: Replace with custom exception
    }

    public UUID newUser(String email, String password, String name) {
        Optional<UserView> previousUser = repository.findByEmail(email);
        previousUser.ifPresent(user -> {
            throw new RuntimeException("User already present");
        });

        UUID userId = UUID.randomUUID();

        CompletableFuture<UUID> newUserId = commandGateway.send(new CreateUserCommand(
                userId,
                email,
                passwordEncoder.encode(password),
                name,
                Set.of(USER_AUTHORITY)
        ));

        return newUserId.join();
    }

    private JWTUserDetails getUserDetails(UserView user, String token) {
        return JWTUserDetails
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()))
                .token(token)
                .build();
    }


    private Optional<DecodedJWT> getDecodedToken(String token) {
        try {
            return Optional.of(verifier.verify(token));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    @Transactional
    public UserView getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String name = authentication.getName();
        Optional<UserView> user = repository.findByEmail(name);
        if (user.isEmpty()) {
            throw new RuntimeException("Unexpected error");
        }
        return user.get();
    }

    @Transactional
    public String getToken(UserView user) {
        Instant now = Instant.now();
        Instant expiry = Instant.now().plus(properties.getTokenExpiration());
        return JWT
                .create()
                .withIssuer(properties.getTokenIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withSubject(user.getEmail())
                .sign(algorithm);
    }
}
