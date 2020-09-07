package com.weber.cms.content;

import com.weber.cms.contact.model.Contact;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ContentController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("backingObject", new Contact());
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String homePost(@ModelAttribute("backingObject") Contact contact,  Model model) {
        System.out.println(contact.getFirstName());
        System.out.println(contact.getLastName());
        model.addAttribute("backingObject", contact);
        return "index";
    }

    @RequestMapping(value = "/home2", method = RequestMethod.GET)
    public ModelAndView home2() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.getModelMap().addAttribute("myAttribute", "My Value");

        return modelAndView;
    }

    @ModelAttribute
    public void myCustomMessage(Model model) {
        model.addAttribute("customMessage", "Awesome");
    }
}
