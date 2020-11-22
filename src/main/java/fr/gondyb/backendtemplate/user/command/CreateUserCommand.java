package fr.gondyb.backendtemplate.user.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private final UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final Set<String> roles;
}
