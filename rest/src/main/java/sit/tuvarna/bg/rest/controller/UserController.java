package sit.tuvarna.bg.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserOperation;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserRequest;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserResponse;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordOperation;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordRequest;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordResponse;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoOperation;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoRequest;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoResponse;
import sit.tuvarna.bg.api.operations.user.login.LoginOperation;
import sit.tuvarna.bg.api.operations.user.login.LoginRequest;
import sit.tuvarna.bg.api.operations.user.login.LoginResponse;
import sit.tuvarna.bg.api.operations.user.register.RegisterOperation;
import sit.tuvarna.bg.api.operations.user.register.RegisterRequest;
import sit.tuvarna.bg.api.operations.user.register.RegisterResponse;
import sit.tuvarna.bg.core.externalservices.XPProgress;
import sit.tuvarna.bg.core.externalservices.XPProgressService;
import sit.tuvarna.bg.core.processor.user.LogoutService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/user")
public class UserController {
    private final LoginOperation login;
    private final RegisterOperation register;
    private final LogoutService logout;
    private final GetUserInfoOperation getUserInfo;
    private final ChangePasswordOperation changePassword;
    private final ArchiveUserOperation archiveUser;

    private final XPProgressService progressService;


    @GetMapping("/info")
    public ResponseEntity<GetUserInfoResponse> getUserInfo(
            @RequestParam @NotBlank(message = "User email is required") String email) {
        GetUserInfoRequest request = GetUserInfoRequest
                .builder()
                .email(email)
                .build();
        return new ResponseEntity<>(getUserInfo.process(request), HttpStatus.OK);
    }

    @GetMapping("/xp-info")
    public ResponseEntity<XPProgress> getXPProgress(@RequestParam int totalXp) {
        XPProgress xpProgress = progressService.calculateXPProgress(totalXp);
        return ResponseEntity.ok(xpProgress);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return new ResponseEntity<>(register.process(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        return new ResponseEntity<>(login.process(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Integer> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        logout.logout(request, response, authentication);
        return ResponseEntity.status(200).build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> logout(
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        return new ResponseEntity<>(changePassword.process(request), HttpStatus.OK);
    }

    @PatchMapping("/archive-user")
    public ResponseEntity<ArchiveUserResponse> archiveUser(
            @RequestBody @Valid ArchiveUserRequest request
    ) {
        return new ResponseEntity<>(archiveUser.process(request), HttpStatus.OK);
    }
}
