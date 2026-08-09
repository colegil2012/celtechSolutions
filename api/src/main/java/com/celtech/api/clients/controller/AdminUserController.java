package com.celtech.api.clients.controller;

import com.celtech.api.clients.auth.AuthPrincipal;
import com.celtech.api.clients.dto.PasswordResetRequest;
import com.celtech.api.clients.dto.UserCreateRequest;
import com.celtech.api.clients.dto.UserDto;
import com.celtech.api.clients.dto.UserUpdateRequest;
import com.celtech.api.clients.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portal/admin/users")
public class AdminUserController {

    private final AdminUserService svc;

    public AdminUserController(AdminUserService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<UserDto> list() {
        return svc.list();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserCreateRequest req) {
        return svc.create(req);
    }

    @PutMapping("/{userId}")
    public UserDto update(@AuthenticationPrincipal AuthPrincipal actor,
                          @PathVariable String userId,
                          @RequestBody UserUpdateRequest req) {
        svc.guardLastAdmin(actor, userId, req);
        return svc.update(userId, req);
    }

    @PostMapping("/{userId}/password")
    public ResponseEntity<Void> resetPassword(@PathVariable String userId,
                                              @Valid @RequestBody PasswordResetRequest req) {
        svc.resetPassword(userId, req.newPassword());
        return ResponseEntity.noContent().build();
    }
}
