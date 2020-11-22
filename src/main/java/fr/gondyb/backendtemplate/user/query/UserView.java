package fr.gondyb.backendtemplate.user.query;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserView {
    @Id
    @Column(name = "user_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    private String email;

    private String password;

    private String name;

    @ElementCollection
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "name")
    private Set<String> roles;
}