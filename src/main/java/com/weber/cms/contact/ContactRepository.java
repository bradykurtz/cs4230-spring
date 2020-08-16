package com.weber.cms.contact;

import java.util.HashMap;
import java.util.Map;

import com.weber.cms.contact.model.Contact;
import org.springframework.stereotype.Repository;

@Repository
public class ContactRepository {

    private static final Map<String, Contact> contacts = new HashMap<>();

    static {
        contacts.put("1", new Contact());
    }

    public Contact getContactById(String id) {
        return contacts.get(id);
    }

}
