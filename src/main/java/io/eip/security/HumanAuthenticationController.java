package io.eip.security;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** HTTP boundary for people; cookies carry opaque session state and never passwords. */
@RestController
@RequestMapping("/api/auth")
public class HumanAuthenticationController {
    private final HumanAuthenticationService authentication;
    private final boolean secureCookies;
    public HumanAuthenticationController(HumanAuthenticationService authentication, @Value("${eip.human-session.secure-cookie:false}") boolean secureCookies) { this.authentication = authentication; this.secureCookies = secureCookies; }

    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> register(@Valid @RequestBody Credentials request) {
        HumanUser user = authentication.register(request.email(), request.password());
        return Map.of("id", user.getId(), "email", user.getEmail());
    }
    @PostMapping("/login")
    Map<String, Object> login(@Valid @RequestBody Credentials request, HttpServletResponse response) {
        HumanAuthenticationService.IssuedSession session = authentication.login(request.email(), request.password());
        writeCookies(response, session);
        return Map.of("expiresInSeconds", session.ttl().toSeconds());
    }
    @GetMapping("/me")
    Map<String, Object> me(@CookieValue(name = "EIP_SESSION", required = false) String token) {
        var human = authentication.authenticate(token);
        return Map.of("id", human.user().getId(), "email", human.user().getEmail());
    }
    @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT)
    void logout(@CookieValue(name = "EIP_SESSION", required = false) String token,
            @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, HttpServletResponse response) {
        authentication.logout(token, csrf); clearCookies(response);
    }
    private void writeCookies(HttpServletResponse response, HumanAuthenticationService.IssuedSession session) {
        long seconds = session.ttl().toSeconds();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie("EIP_SESSION", session.token(), true, seconds).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie("EIP_CSRF", session.csrfToken(), false, seconds).toString());
    }
    private void clearCookies(HttpServletResponse response) { response.addHeader(HttpHeaders.SET_COOKIE, cookie("EIP_SESSION", "", true, 0).toString()); response.addHeader(HttpHeaders.SET_COOKIE, cookie("EIP_CSRF", "", false, 0).toString()); }
    private ResponseCookie cookie(String name, String value, boolean httpOnly, long maxAge) { return ResponseCookie.from(name, value).httpOnly(httpOnly).secure(secureCookies).sameSite("Strict").path("/").maxAge(Duration.ofSeconds(maxAge)).build(); }
    record Credentials(@Email @NotBlank String email, @NotBlank @Size(min = 12, max = 128) String password) { }
}
