package com.user.service.dto;

import com.user.service.Entidades.Role;

public class RegisterResponseDTO {
    private Long id;
    private String username;
    private String displayName;
    private Role role;

    public RegisterResponseDTO(Long id, String username, String displayName, Role role) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
}
