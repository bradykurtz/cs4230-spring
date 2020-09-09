package com.weber.cms.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.weber.cms.contact.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContactRepository {

//    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

//    @Autowired
//    public ContactRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
//        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
//    }

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ContactRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Map<String, Contact> contacts = new HashMap<>();

    public Contact getContactById(String id) {
        String sql = "SELECT * FROM CONTACT WHERE id = ?";
        List<Object> values = new ArrayList<>();
        values.add(id);

        return jdbcTemplate.queryForObject(sql, values.toArray(), (RowMapper<Contact>) (rs, rowNum) -> {
            Contact contact = new Contact();
            contact.setFirstName(rs.getString("FIRST_NAME"));
            contact.setLastName(rs.getString("LAST_NAME"));
            contact.setId(rs.getString("id"));
            return contact;
        });
    }

//    public Contact getContactById(String id) {
//        String sql = "SELECT * FROM CONTACT WHERE id = :id";
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("id", id);
//        return namedParameterJdbcTemplate.queryForObject(sql, parameters, (RowMapper<Contact>) (rs, rowNum) -> {
//           Contact contact = new Contact();
//           contact.setFirstName(rs.getString("FIRST_NAME"));
//           contact.setLastName(rs.getString("LAST_NAME"));
//           contact.setId(rs.getString("id"));
//           return contact;
//        });
//    }

    public Contact recordContact(Contact contact) {

        if(contact.getId() == null) {
            contact.setId(UUID.randomUUID().toString());
        }

        contacts.put(contact.getId(), contact);
        return contact;
    }

}
