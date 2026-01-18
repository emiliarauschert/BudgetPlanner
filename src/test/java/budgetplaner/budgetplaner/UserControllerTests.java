package budgetplaner.budgetplaner;

import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.*;
import budgetplaner.budgetplaner.Security.*;
import budgetplaner.budgetplaner.Repository.UserRepository;
import budgetplaner.budgetplaner.Security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;


    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        user = new User();
        user.setName("TestUser1");
        user.setEmail("test@test1.com");
        user.setPassword(passwordEncoder.encode("test123"));
        userRepository.save(user);
    }


    @Test
    void registerTest() throws Exception {
        String json = """
                {
                  "name": "test",
                  "email": "test@test.com",
                  "password": "test123"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Registrierung erfolgreich"));

    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void register_twice() throws Exception {
        String json = """
                {
                  "name": "TestUser1",
                  "email": "test@test1.com",
                  "password": "test123"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(status().reason("E-Mail ist bereits registriert"));


    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void loginTest() throws Exception {

        Mockito.when(jwtService.generateToken("test@test1.com"))
                .thenReturn("fake-jwt-token");

        String json = """
                {
                  "email": "test@test1.com",
                  "password": "test123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.name").value("TestUser1"))
                .andExpect(jsonPath("$.email").value("test@test1.com"));
    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void loginTest_wrongPassword() throws Exception {

        String json = """
                {
                  "email": "test@test.com",
                  "password": "wrong"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());

    }


}