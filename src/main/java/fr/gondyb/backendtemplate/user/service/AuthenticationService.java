package fr.gondyb.backendtemplate.user.service;

import fr.gondyb.backendtemplate.user.event.UserAuthenticatedEvent;
import fr.gondyb.backendtemplate.user.query.UserView;
import fr.gondyb.backendtemplate.user.query.UserViewRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationProvider authenticationProvider;
    private final EventGateway eventGateway;
    private final UserViewRepository userRepository;

    public void authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                email,
                password
        );

        try {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authenticate = authenticationProvider.authenticate(credentials);
            context.setAuthentication(authenticate);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException(email);
        }

        Optional<UserView> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new RuntimeException("Unexpected Error");
        }

        eventGateway.publish(new UserAuthenticatedEvent(
                user.get().getId()
        ));
    }
}
