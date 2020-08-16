package com.weber.cms.contact.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(
    value = "Api Model Value",
    description = "Api Model Description",
    parent = Object.class,
    discriminator = "Api Discriminator",
    subTypes = { Contact.class },
    reference = "Api Reference"
)
public class Contact {

    @ApiModelProperty(value="notFirstName",
        allowableValues = "Allow Values",
        access = "Read Write",
        notes = "My note",
        dataType = "Number",
        required = true,
        hidden = false)
    private String firstName;

    private String lastName;

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
}
