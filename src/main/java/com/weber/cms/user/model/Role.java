package com.weber.cms.user.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Role {

    private UUID id;

    private String name;

    private Map<UUID, Permission> permissions;

    public Permission getPermissionById(UUID id) {
        if (permissions != null) {
            return permissions.get(id);
        }
        return null;
    }

    public Map<UUID, Permission> getPermissions() {
        if (permissions == null) {
            permissions = new HashMap<>();
        }
        return permissions;
    }

    public void setPermissions(Map<UUID, Permission> permissions) {
        this.permissions = permissions;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
