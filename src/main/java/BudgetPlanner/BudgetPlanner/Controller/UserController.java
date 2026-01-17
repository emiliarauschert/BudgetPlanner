package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Security.JwtService;
import BudgetPlanner.BudgetPlanner.dto.LoginRequest;
import BudgetPlanner.BudgetPlanner.dto.SignUpRequest;
import BudgetPlanner.BudgetPlanner.dto.UpdateEmailRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    //Login (Post)
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {

        // @Valid checkt email/password schon auf NotBlank + Email (im DTO)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User nicht gefunden"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falsches Passwort");
        }

        String token = jwtService.generateToken(user.getEmail());
        return Map.of(
                "token", token,
                "name", user.getName(),
                "email", user.getEmail()
        );
    }

    //Registrieren (Post)
    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody SignUpRequest request) {

        // @Valid checkt name/email/password (inkl. Email-Format + Passwortlänge im DTO)
        boolean exists = userRepository.findByEmail(request.email()).isPresent();
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-Mail ist bereits registriert");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return Map.of("message", "Registrierung erfolgreich");
    }

    // Get (mein Account)
    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nicht eingeloggt");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User nicht gefunden"));

        return Map.of(
                "name", user.getName(),
                "email", user.getEmail()
        );
    }

    // Update (Email)
    @PutMapping("/me/email")
    public Map<String, String> updateEmail(
            @Valid @RequestBody UpdateEmailRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nicht eingeloggt");
        }

        String currentEmail = authentication.getName();

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User nicht gefunden"));

        // Passwort prüfen
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwort ist falsch");
        }

        // Konfliktcheck
        boolean newEmailExists = userRepository.findByEmail(request.newEmail()).isPresent();
        if (newEmailExists && !request.newEmail().equalsIgnoreCase(currentEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-Mail ist bereits registriert");
        }

        user.setEmail(request.newEmail());
        userRepository.save(user);

        // neuen Token ausstellen (subject=email)
        String newToken = jwtService.generateToken(user.getEmail());

        return Map.of(
                "token", newToken,
                "email", user.getEmail(),
                "name", user.getName()
        );
    }
}
