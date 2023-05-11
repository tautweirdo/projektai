package dev.taut.websitedemo.Security;

import dev.taut.websitedemo.Accounts.Account;
import dev.taut.websitedemo.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new Account());
        return "registration";
    }

    @PostMapping("/security/registration")
    public String addUser(@ModelAttribute("user") @Valid Account user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("passwordError", "Slaptažodžiai nesutampa");
            return "registration";
        }
        if (!RegistrationService.saveAccount(user)) {
            model.addAttribute("usernameError", "Paskyra su tokiu slaptažodiu jau egzistuoja");
            return "registration";
        }
        return "redirect:/login";
    }
}

