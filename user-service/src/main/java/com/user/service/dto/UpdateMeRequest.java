package com.user.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateMeRequest {

    @NotBlank
    @Size(min = 2, max = 80)
    private String displayName;

    // opcional (si quieres permitir cambiar email)
    @Email
    @Size(max = 190)
    private String email;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}