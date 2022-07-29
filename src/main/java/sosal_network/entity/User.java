package sosal_network.entity;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sosal_network.Enum.Role;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Class User - класс сущности пользователя
 **/
@Entity(name = "user")
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    /**
     * имя пользователя
     **/
    private String username;
//    /** фамилия пользователя  **/
//    private String userSurname;

    /**
     * пароль пользователя
     **/
    private String password;

    /**
     * подтвержденный пароль пользователя
     **/
    @Transient
    private String userPasswordConfirm;

    /**
     * почта пользователя
     **/
    private String userEmail;

    /**
     * активность пользователя
     **/
    private Boolean active;

    /**
     * дата регистрации пользователя
     **/
    private LocalDate registrationDate;

    /**
     * Соединение таблиц пользователя с ролями
     * author - Nikita
     **/
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    /**
     * Соединение таблиц пользователя с друзьями
     * author - Nikita
     **/
    @OneToMany(mappedBy = "userID", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Friend> friendsList = new HashSet<Friend>();


    /**
     * Основной конструктор
     **/
    public User(String username, String password, String passwordConfirm, String email) {
        this.username = username;
        this.password = password;
        this.userPasswordConfirm = passwordConfirm;
        this.userEmail = email;
        this.registrationDate = LocalDate.now();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}