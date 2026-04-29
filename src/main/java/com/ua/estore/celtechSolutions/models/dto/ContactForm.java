package com.ua.estore.celtechSolutions.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactForm {
    @NotBlank(message = "Tell us your name.")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "Email to contact.")
    @Email(message = "Please provide a valid email address.")
    @Size(max = 200)
    private String email;

    @Size(max = 120)
    private String company;

    @NotBlank(message = "A subject is helpful, but not required.")
    @Size(max = 160)
    private String subject;

    @NotBlank(message = "The Message is required, what you want?")
    @Size(min = 10, max = 4000, message = "Messages should be between 10 and 4000 characters.")
    private String message;
}
