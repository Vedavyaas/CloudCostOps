package com.pheonix.authenticationsystem.controller;

import com.pheonix.authenticationsystem.assets.*;
import com.pheonix.authenticationsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserActionController {
    private final UserService userService;

    public UserActionController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/api/user/create/company")
    public ResponseEntity<String> createCompany(@RequestBody CompanyDetails companyDetails) {
        return ResponseEntity.ok(userService.createCompany(companyDetails));
    }

    @PutMapping("/admin/create/analyst")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<String> createAnalyst(@RequestBody AnalystDetails analystDetails, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.createAnalyst(analystDetails, jwt.getClaim("company-name")));
    }

    @PutMapping("/api/user/login")
    public ResponseEntity<JWTToken> authenticate(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.authenticate(loginRequest));
    }

    @GetMapping("/get/info")
    public ResponseEntity<UserDTO> getSelfInfo(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getInfo(jwt.getSubject()));
    }

    @GetMapping("/admin/get/analyst/count")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<String> getCount(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getAnalystCount(jwt.getClaim("company-name")));
    }

    @GetMapping("/admin/get/analyst")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAnalysts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getAnalysts(jwt.getClaim("company-name")));
    }

    @ExceptionHandler(CredentialsAlreadyExistsException.class)
    public ResponseEntity<String> handle(CredentialsAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
