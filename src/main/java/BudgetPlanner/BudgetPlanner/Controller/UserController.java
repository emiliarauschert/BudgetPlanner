package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Security.JwtService;
import BudgetPlanner.BudgetPlanner.dto.LoginRequest;
import BudgetPlanner.BudgetPlanner.dto.SignUpRequest;
import org.springframework.http.HttpStatus;
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

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        if (request.email() == null || request.password() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email/Passwort fehlen");
        }

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

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody SignUpRequest request) {

        if (request.email() == null || request.password() == null || request.name() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name/Email/Passwort fehlen");
        }

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
}
