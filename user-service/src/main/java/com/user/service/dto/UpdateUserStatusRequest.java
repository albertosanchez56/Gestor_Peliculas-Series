package com.user.service.dto;

import com.user.service.Entidades.Status;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(@NotNull Status status) {}
