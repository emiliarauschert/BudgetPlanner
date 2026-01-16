package BudgetPlanner.BudgetPlanner;
import BudgetPlanner.BudgetPlanner.Modell.SavingsGoal;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.*;
import BudgetPlanner.BudgetPlanner.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SavingsGoalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SavingsRepository savingsRepository;


    @MockitoBean
    private SavingsGoalService savingsGoalService;




    @BeforeEach
    void setUp() {
        savingsRepository.deleteAll();
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
    void updateGoalTest() throws Exception {

        SavingsGoal updated = new SavingsGoal();
        updated.setTitle("Neues Auto");
        updated.setTargetAmount(12000);
        updated.setCurrentAmount(2000);
        updated.setDeadline(LocalDate.of(2026, 6, 1));

        Mockito.when(
                savingsGoalService.update(Mockito.eq(1L), Mockito.any(), Mockito.any(User.class))
        ).thenReturn(updated);



        String json = """
            {
              "title": "Neues Auto",
              "targetAmount": 12000,
              "currentAmount": 2000,
              "deadline": "2026-06-01"
            }
            """;

        mockMvc.perform(put("/api/savings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Neues Auto"))
                .andExpect(jsonPath("$.targetAmount").value(12000))
                .andExpect(jsonPath("$.currentAmount").value(2000));
    }
}
