package com.email.writer.Email_vapp;

import com.email.writer.Service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class emailGeneratorController {

    @Autowired
    private final EmailService emailService;

    @PostMapping("/generate")
    ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response=emailService.GenerateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }

}
