package com.user.service.dto;

import com.user.service.Entidades.Role;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(@NotNull Role role) {}

