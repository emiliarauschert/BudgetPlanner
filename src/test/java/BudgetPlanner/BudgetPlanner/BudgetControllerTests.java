package BudgetPlanner.BudgetPlanner;

import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BudgetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        budgetRepository.deleteAll();
        stockRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("TestUser");
        user.setEmail("test@test1.com");

        user.setPassword(passwordEncoder.encode("test123"));
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void addBudgetTest() throws Exception {

        String json = """
        {
          "category": "FOOD",
          "limitAmount": 500,
          "month": "2025-01"
        }
        """;

        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.limitAmount").value(500));
    }


    @Test
    @WithMockUser(username = "test@test1.com")
    void clearBudgetsTest() throws Exception {

        String json = """
        {
          "category": "FOOD",
          "limitAmount": 500,
          "month": "2025-01"
        }
        """;

        String response = mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.id");
        Long budgetId = id.longValue();

        mockMvc.perform(delete("/api/budgets/" + id))
                .andExpect(status().isOk());


    }


}
