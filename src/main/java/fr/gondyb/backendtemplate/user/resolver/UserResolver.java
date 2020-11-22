package fr.gondyb.backendtemplate.user.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import fr.gondyb.backendtemplate.user.query.UserView;
import fr.gondyb.backendtemplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResolver implements GraphQLResolver<UserView> {
    private final UserService service;

    @PreAuthorize("isAuthenticated()")
    public String getToken(UserView user) {
        return service.getToken(user);
    }

    public String getName(UserView user) {
        return user.getName();
    }

    public String getEmail(UserView user) {
        return user.getEmail();
    }
}
