package fr.gondyb.backendtemplate;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import fr.gondyb.backendtemplate.user.query.UserView;
import fr.gondyb.backendtemplate.user.query.UserViewRepository;
import fr.gondyb.backendtemplate.user.service.AuthenticationService;
import fr.gondyb.backendtemplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MutationResolver implements GraphQLMutationResolver {
    private final UserService userService;

    private final AuthenticationService authenticationService;

    private final UserViewRepository userRepository;

    @PreAuthorize("isAnonymous()")
    public UserView login(String email, String password) {
        try {
            authenticationService.authenticate(email, password);
            return userService.getCurrentUser();
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException(email);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public UserView newUser(String email, String password, String name) {
        UUID uuid = userService.newUser(email, password, name);
        return userRepository.findById(uuid).orElseThrow(IllegalArgumentException::new);
    }
}
