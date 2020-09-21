package com.weber.cms.user.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.weber.cms.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UserRepository {

    private static final String USER_ID_KEY = "id";
    private static final String USER_ID_BINDING_KEY = ":" + USER_ID_KEY;
    private static final String USERNAME_KEY = "username";
    private static final String USERNAME_BINDING_KEY = ":" + USERNAME_KEY;
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String FIRST_NAME_BINDING_KEY = ":" + FIRST_NAME_KEY;
    private static final String LAST_NAME_KEY = "last_name";
    private static final String LAST_NAME_BINDING_KEY = ":" + LAST_NAME_KEY;
    private static final String PASSWORD_KEY = "password";
    private static final String PASSWORD_BINDING_KEY = ":" + PASSWORD_KEY;
    private static final String EMAIL_KEY = "email";
    private static final String EMAIL_BINDING_KEY = ":" + EMAIL_KEY;
    private static final String LOCKED_KEY = "locked";
    private static final String LOCKED_BINDING_KEY = ":" + LOCKED_KEY;
    private static final String ENABLED_KEY = "enabled";
    private static final String ENABLED_BINDING_KEY = ":" + ENABLED_KEY;
    private static final String CREDS_EXPIRE_ON_KEY = "creds_expire_on";
    private static final String CREDS_EXPIRE_ON_BINDING_KEY = ":" + CREDS_EXPIRE_ON_KEY;
    private static final String EXPIRED_ON_KEY = "expired_on";
    private static final String EXPIRED_ON_BINDING_KEY = ":" + EXPIRED_ON_KEY;
    private static final String CREATED_ON_KEY = "created_on";
    private static final String CREATED_ON_BINDING_KEY = ":" + CREATED_ON_KEY;
    private static final String MODIFIED_ON_KEY  = "modified_on";
    private static final String MODIFIED_ON_BINDING_KEY = ":" + MODIFIED_ON_KEY;

    private static final List<String> primaryKeys = new ArrayList<>();

    static {
        primaryKeys.add(USER_ID_KEY);
        primaryKeys.add(USER_ID_BINDING_KEY);
    }

    private static final String SELECT_USER_WITH_ROLES_SQL = "SELECT BIN_TO_UUID(u.id) as user_id, "
        + "u.first_name, "
        + "u.last_name, "
        + "u.username, "
        + "u.password, "
        + "u.email, "
        + "u.locked, "
        + "u.enabled, "
        + "u.creds_expire_on, "
        + "u.expired_on, "
        + "u.created_on, "
        + "u.modified_on, "
        + "r.id as role_id, "
        + "r.name as role_name, "
        + "p.id as perm_id, "
        + "p.name as perm_name "
        + "FROM USER u "
        + "LEFT JOIN USER_ROLE ur ON ur.user_id = u.id "
        + "LEFT JOIN ROLE r ON ur.role_id = r.id "
        + "LEFT JOIN ROLE_PERMISSION rp ON r.id = rp.role_id "
        + "LEFT JOIN PERMISSION p ON rp.permission_id = p.id "
        + "WHERE 1=1 ";

    private static final String RECORD_USER = "INSERT INTO ("
        + "first_name, "
        + "last_name,"
        + "username,"
        + "password,"
        + "email"
        + "locked"
        + "enabled"
        + "creds_expire_on"
        + "expired_on"
        + "modified_on"
        + ")";

    private static final String WHERE_USERNAME_OR_EMAIL_EQUALS_USERNAME = "u.username = " + USERNAME_BINDING_KEY + " OR u.email = " + USERNAME_BINDING_KEY;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User record(User user) {
        List<String> sqlColumns = new ArrayList<>();
        Map<String, String> columnNameToBindingValue = new LinkedHashMap<>(); //Important to Keep Insertion Order
        MapSqlParameterSource bindingValues = new MapSqlParameterSource();

        addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, USER_ID_KEY, USER_ID_BINDING_KEY, user.getId().toString());

        addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, FIRST_NAME_KEY, FIRST_NAME_BINDING_KEY, user.getFirstName());

        addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, LAST_NAME_KEY, LAST_NAME_BINDING_KEY, user.getLastName());

        addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, USERNAME_KEY, USERNAME_BINDING_KEY, user.getUsername());

        if (user.getPassword() != null) {
            addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, PASSWORD_KEY, PASSWORD_BINDING_KEY, user.getPassword());
        }

        addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, EMAIL_KEY, EMAIL_BINDING_KEY, user.getEmail());

        if (user.isLocked()) {
            addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, LOCKED_KEY, LOCKED_BINDING_KEY, user.isLocked());
        }

        if(!user.isEnabled()) {
            addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, ENABLED_KEY, ENABLED_BINDING_KEY, user.isEnabled());
        }

        if(user.getCredentialsExpireOn() != null) {
            addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, CREDS_EXPIRE_ON_KEY, CREDS_EXPIRE_ON_BINDING_KEY, Timestamp
                .from(user.getCredentialsExpireOn().toInstant()));
        }

        if(user.getExpiredOn() != null) {
            addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, EXPIRED_ON_KEY, EXPIRED_ON_BINDING_KEY, Timestamp
                .from(user.getExpiredOn().toInstant()));
        }

        addSqlItem(sqlColumns, columnNameToBindingValue, bindingValues, MODIFIED_ON_KEY, MODIFIED_ON_BINDING_KEY, Timestamp
            .from(user.getModifiedOn().toInstant()));

        String insertionValueString = columnNameToBindingValue.values().stream().map(s -> {
            if (primaryKeys.contains(s)) {
                return "UUID_TO_BIN(" + s +", true)";
            }
            return s;
        }).collect(Collectors.joining(","));

        String onDuplicateKeyString = columnNameToBindingValue.entrySet().stream()
            .filter(es -> !primaryKeys.contains(es.getKey()))
            .map(es -> es.getKey() + " = " + es.getValue())
            .collect(Collectors.joining(","));

        StringBuilder recordSql = new StringBuilder("INSERT INTO USER (")
            .append(String.join(",", sqlColumns))
            .append(") ")
            .append("VALUES (")
            .append(insertionValueString)
            .append(") ON DUPLICATE KEY UPDATE ")
            .append(onDuplicateKeyString);

        jdbcTemplate.update(recordSql.toString(), bindingValues);

        return user;
    }

    public User getUserByUsername(String username) {
        String sql = SELECT_USER_WITH_ROLES_SQL + " AND " +
            WHERE_USERNAME_OR_EMAIL_EQUALS_USERNAME;
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue(USERNAME_KEY, username);
        UserRowCallbackHandler callbackHandler = new UserRowCallbackHandler();
        jdbcTemplate.query(sql, parameterSource, callbackHandler);
        return callbackHandler.getUser();
    }

    private void addSqlItem(List<String> columns,
        Map<String, String> sqlValues,
        MapSqlParameterSource bindingValues,
        String columnName,
        String bindingKey,
        Object value) {
        columns.add(columnName);
        sqlValues.put(columnName, bindingKey);
        bindingValues.addValue(columnName, value);
    }
}
