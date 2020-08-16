package com.weber.cms.contact;

import com.weber.cms.contact.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    @Autowired //constructor injection
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact getContactById(String id) {
        Contact contact = contactRepository.getContactById(id);
        if(contact == null) {
            throw new RuntimeException("Contact Not Found"); //Not Good Practice to throw straight run time exceptions
        }
        return contact;
    }

    public Contact recordContact(Contact contact) {
        return contactRepository.recordContact(contact);
    }

}
