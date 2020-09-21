package com.weber.cms.user;

import com.weber.cms.spring.validation.ValidationGroup;
import com.weber.cms.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView loginUser() {
        ModelAndView modelAndView = new ModelAndView("user/login");
        modelAndView.getModelMap().addAttribute("user", new User());
        return modelAndView;
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public ModelAndView registerUser() {
        ModelAndView modelAndView = new ModelAndView("user/register");
        modelAndView.getModelMap().addAttribute("user", new User());
        return modelAndView;
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView registerUser(@ModelAttribute("user") @Validated(value = { ValidationGroup.Register.class }) User user, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("user/register");
        } else {
            modelAndView.setViewName("redirect:/user/login");
            userService.recordUser(user, true);
        }

        return modelAndView;
    }

}
