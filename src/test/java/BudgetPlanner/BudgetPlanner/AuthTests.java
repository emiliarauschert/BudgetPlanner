package BudgetPlanner.BudgetPlanner;

import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.*;
import BudgetPlanner.BudgetPlanner.Security.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthTests {

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
    }


    @Test
    @WithMockUser(username = "test@test.com")
    void registerTest() throws Exception {
        String json = """
                {
                  "name": "test",
                  "email": "test@tester.com",
                  "password": "test"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "test@test.com")
    void loginTest() throws Exception {

        Mockito.when(jwtService.generateToken("test@test.com"))
                .thenReturn("fake-jwt-token");

        String json = """
                {
                  "email": "test@test.com",
                  "password": "test"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.name").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
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