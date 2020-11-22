package fr.gondyb.backendtemplate.user.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class UserCreatedEvent {
    private final UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final Set<String> roles;
}
