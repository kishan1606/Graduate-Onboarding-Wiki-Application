package com.cooksys.groupfinal.controllers.advice;

import com.cooksys.groupfinal.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
@CrossOrigin(origins="*")
public class ValidateController {

    private final ValidateService validateService;

    @GetMapping("/username/available/{username}")
    public boolean isUsernameAvailable(@PathVariable String username) {
        return validateService.isUsernameAvailable(username);
    }

    @GetMapping("/username/exists/{username}")
    public boolean usernameExists(@PathVariable String username) {
        return validateService.usernameExists(username);
    }
}

