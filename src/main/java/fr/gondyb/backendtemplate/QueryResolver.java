package fr.gondyb.backendtemplate;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import fr.gondyb.backendtemplate.user.query.UserView;
import fr.gondyb.backendtemplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryResolver implements GraphQLQueryResolver {
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    public UserView getCurrentUser() {
        return userService.getCurrentUser();
    }

}
