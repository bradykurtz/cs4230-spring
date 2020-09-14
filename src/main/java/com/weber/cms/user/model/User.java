package com.weber.cms.user.model;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

    private UUID id;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String username;

    private boolean locked;

    private boolean enabled;

    private ZonedDateTime credentialsExpireOn;

    private ZonedDateTime expiredOn;

    private ZonedDateTime createdOn;

    private ZonedDateTime modifiedOn;

    private Map<UUID, Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(this.roles)
            .orElseGet(HashMap::new)
            .values()
            .stream()
            .filter(role ->  role.getPermissions() != null && role.getPermissions().size() > 0)
            .flatMap(role -> role.getPermissions().values().stream())
            .filter(Objects::nonNull)
            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return expiredOn == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (credentialsExpireOn != null) {
            return ZonedDateTime.now().isAfter(credentialsExpireOn);
        } else {
            return true;
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getCredentialsExpireOn() {
        return credentialsExpireOn;
    }

    public void setCredentialsExpireOn(ZonedDateTime credentialsExpireOn) {
        this.credentialsExpireOn = credentialsExpireOn;
    }

    public ZonedDateTime getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(ZonedDateTime expiredOn) {
        this.expiredOn = expiredOn;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(ZonedDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Map<UUID, Role> getRoles() {
        if (roles == null) {
            roles = new HashMap<>();
        }
        return roles;
    }

    public void setRoles(Map<UUID, Role> roles) {
        this.roles = roles;
    }
}
