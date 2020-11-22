package fr.gondyb.backendtemplate.user.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserAuthenticatedEvent {
    private final UUID id;
}
