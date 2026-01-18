package budgetplaner.budgetplaner;

import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.*;
import budgetplaner.budgetplaner.Repository.IncomeRepository;
import budgetplaner.budgetplaner.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IncomeControllerTests {

    @Autowired
    private MockMvc mockMvc;


    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("TestUser");
        user.setEmail("test@test1.com");

        user.setPassword(passwordEncoder.encode("test123"));
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void getIncomeTest() throws Exception {
        String json = """
        {
          "title":"Geld",
          "amount": 1000,
          "category": "GELD",
          "date": "2025-01-01"
          
        }
        """;

        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Geld"))
                .andExpect(jsonPath("$.category").value("GELD"))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.date").value("2025-01-01"));


    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void clearIncomeTest() throws Exception {

        String json = """
        { "title":"Geld",
          "category": "GELD",
          "amount": 500,
          "date": "2025-01-01"
          
        }
        """;

        String response = mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.id");
        Long budgetId = id.longValue();

        mockMvc.perform(delete("/api/income/" + budgetId))
                .andExpect(status().isOk());

    }

}
