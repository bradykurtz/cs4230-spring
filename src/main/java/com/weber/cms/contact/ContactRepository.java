package com.weber.cms.contact;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public Contact recordContact(Contact contact) {

        if(contact.getId() == null) {
            contact.setId(UUID.randomUUID().toString());
        }

        contacts.put(contact.getId(), contact);
        return contact;
    }

}
