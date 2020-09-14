package com.weber.cms.user.repository;

import com.weber.cms.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UserRepository {

    private static final String USERNAME_KEY = ":USERNAME";

    private static final String SELECT_USER_WITH_ROLES_SQL = "SELECT u.id as user_id, "
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
        + "INNER JOIN USER_ROLE ur ON ur.user_id = u.id "
        + "INNER JOIN ROLE r ON ur.role_id = r.id "
        + "INNER JOIN ROLE_PERMISSION rp ON r.id = rp.role_id "
        + "INNER JOIN PERMISSION p rp.permission_id = p.id"
        + "WHERE 1=1 ";

    private static final String WHERE_USERNAME_OR_EMAIL_EQUALS_USERNAME = "u.username = " + USERNAME_KEY + "OR u.email = " + USERNAME_KEY;

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserByUsername(String username) {
        String sql = SELECT_USER_WITH_ROLES_SQL +
            WHERE_USERNAME_OR_EMAIL_EQUALS_USERNAME;
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue(USERNAME_KEY, username);
        UserRowCallbackHandler callbackHandler = new UserRowCallbackHandler();
        jdbcTemplate.query(sql, parameterSource, callbackHandler);
        return callbackHandler.getUser();
    }

}
