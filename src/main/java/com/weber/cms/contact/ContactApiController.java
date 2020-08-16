package com.weber.cms.contact;

import com.weber.cms.contact.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("contact")
public class ContactApiController {

    private ContactService contactService;

    @Autowired
    public ContactApiController(ContactService contactService) {
        this.contactService = contactService;
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "/{id}",
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<Contact> getContact(@PathVariable String id) {

        return ResponseEntity.ok().body(contactService.getContactById(id));
    }

    @RequestMapping(
        method = RequestMethod.POST,
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<Contact> createContent(@RequestBody Contact contact) {
        return null;
    }

}
