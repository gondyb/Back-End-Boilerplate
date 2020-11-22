package fr.gondyb.backendtemplate.user.query;

import fr.gondyb.backendtemplate.user.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserEventHandler {
    private final UserViewRepository userRepository;

    @EventHandler
    public void on(UserCreatedEvent event) {
        UserView user = new UserView(
                event.getId(),
                event.getEmail(),
                event.getPassword(),
                event.getName(),
                event.getRoles()
        );

        userRepository.save(user);
    }
}
