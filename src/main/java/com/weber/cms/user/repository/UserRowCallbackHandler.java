package com.weber.cms.user.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.weber.cms.user.model.Permission;
import com.weber.cms.user.model.Role;
import com.weber.cms.user.model.User;
import org.springframework.jdbc.core.RowCallbackHandler;

public class UserRowCallbackHandler implements RowCallbackHandler {

    private User user;
    private boolean processedLocked = false;
    private boolean processedEnabled = false;
    private boolean processedCredentialsExpired = false;
    private boolean processedExpiredOn = false;

    @Override
    public void processRow(ResultSet rs) throws SQLException {

        if (user == null) {
            user = new User();
        }

        if (user.getId() == null) {
            user.setId(getUUIDFromResultSet(rs, "user_id"));
        }

        if (user.getFirstName() == null) {
            user.setFirstName(rs.getString("first_name"));
        }

        if (user.getLastName() == null) {
            user.setLastName(rs.getString("last_name"));
        }

        if (user.getUsername() == null) {
            user.setUsername(rs.getString("username"));
        }

        if (user.getPassword() == null) {
            user.setPassword(rs.getString("password"));
        }

        if (user.getEmail() == null) {
            user.setEmail(rs.getString("email"));
        }

        if (!processedLocked) {
            user.setLocked(rs.getBoolean("locked"));
            processedLocked = true;
        }

        if (!processedEnabled) {
            user.setEnabled(rs.getBoolean("enabled"));
            processedEnabled = true;
        }

        if (user.getCredentialsExpireOn() == null  && !processedCredentialsExpired) {
            user.setCredentialsExpireOn(createZonedDateTime(rs, "creds_expire_on"));
            processedCredentialsExpired = true;
        }

        if (user.getExpiredOn() == null && !processedExpiredOn) {
            user.setExpiredOn(createZonedDateTime(rs, "expired_on"));
            processedExpiredOn = true;
        }

        if (user.getCreatedOn() == null) {
            user.setCreatedOn(createZonedDateTime(rs, "created_on"));
        }

        if (user.getModifiedOn() == null) {
            user.setModifiedOn(createZonedDateTime(rs, "modified_on"));
        }

        UUID roleId = getUUIDFromResultSet(rs, "role_id");
        UUID permissionId = getUUIDFromResultSet(rs, "perm_id");
        Role currentRole = user.getRoles().get(roleId);

        if (currentRole == null && roleId != null) {
            currentRole = new Role();
            currentRole.setId(roleId);
            currentRole.setName(rs.getString("role_name"));
            user.getRoles().put(roleId, currentRole);
        }

        if(currentRole != null) {
            Permission currentPermission = currentRole.getPermissionById(permissionId);

            if (currentPermission == null) {
                currentPermission = new Permission();
                currentPermission.setId(permissionId);
                currentPermission.setName(rs.getString("perm_name"));
                currentRole.getPermissions().put(permissionId, currentPermission);
            }
        }

    }

    private UUID getUUIDFromResultSet(ResultSet rs, String key) throws SQLException {
        String uuid = rs.getString(key);
        if (uuid != null) {
            return UUID.fromString(uuid);
        }
        return null;
    }

    private ZonedDateTime createZonedDateTime(ResultSet rs, String key) throws SQLException {
        Timestamp value = rs.getTimestamp(key);
        if (value != null) {
            return ZonedDateTime.ofInstant(rs.getTimestamp(key).toInstant(), ZoneId.of("UTC"));
        }
        return null;
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

}
