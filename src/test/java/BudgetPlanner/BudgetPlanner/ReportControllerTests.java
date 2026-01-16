package BudgetPlanner.BudgetPlanner;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.*;
import BudgetPlanner.BudgetPlanner.Service.*;
import BudgetPlanner.BudgetPlanner.Security.*;
import BudgetPlanner.BudgetPlanner.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private SavingsGoalService savingsGoalService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    FinnhubService finnhubService;



    @BeforeEach
    void setUp() {
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
    void getReportTest() throws Exception {

        ReportResponse response = new ReportResponse(
                "2025-01",
                3000.0,
                1200.0,
                List.of(
                        new ReportRow("FOOD", 500.0, 450.0),
                        new ReportRow("RENT", 800.0, 800.0)
                )
        );

        Mockito.when(
                reportService.buildReport(Mockito.any(User.class), Mockito.anyString())
        ).thenReturn(response);

        mockMvc.perform(get("/api/report")
                        .param("month", "2025-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2025-01"))
                .andExpect(jsonPath("$.incomeSum").value(3000.0))
                .andExpect(jsonPath("$.expenseSum").value(1200.0))
                .andExpect(jsonPath("$.rows.length()").value(2))
                .andExpect(jsonPath("$.rows[0].category").value("FOOD"))
                .andExpect(jsonPath("$.rows[0].budgetLimit").value(500.0))
                .andExpect(jsonPath("$.rows[0].spent").value(450.0));

    }

}
