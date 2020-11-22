package fr.gondyb.backendtemplate.user.command;

import fr.gondyb.backendtemplate.user.event.UserCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class UserAggregate {

    @AggregateIdentifier
    private UUID id;

    @CommandHandler
    public UserAggregate(CreateUserCommand command) {
        apply(new UserCreatedEvent(
                command.getId(),
                command.getEmail(),
                command.getPassword(),
                command.getName(),
                command.getRoles()
        ));
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
        this.id = event.getId();
    }
}
