package BudgetPlanner.BudgetPlanner;
import BudgetPlanner.BudgetPlanner.Modell.SavingsGoal;
import BudgetPlanner.BudgetPlanner.Modell.*;
import BudgetPlanner.BudgetPlanner.Repository.*;
import BudgetPlanner.BudgetPlanner.Service.*;
import BudgetPlanner.BudgetPlanner.Security.*;
import BudgetPlanner.BudgetPlanner.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StockControllerTests {

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
    private StockService stockService;

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
    void addStock_success() throws Exception {

        Stock savedStock = new Stock();
        savedStock.setId(1L);
        savedStock.setSymbol("AAPL");
        savedStock.setTvSymbol("NASDAQ:AAPL");
        savedStock.setQuantity(10);
        savedStock.setBuyPrice(150);
        savedStock.setBuyDate(LocalDate.now());
        savedStock.setUser(user);

        Mockito.when(stockService.saveStock(Mockito.any(Stock.class)))
                .thenReturn(savedStock);

        String requestJson = """
                {
                  "symbol": "AAPL",
                  "tvSymbol": "NASDAQ:AAPL",
                  "quantity": 10,
                  "buyPrice": 150
                }
                """;

        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.tvSymbol").value("NASDAQ:AAPL"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.buyPrice").value(150));
    }

    @Test
    @WithMockUser(username = "test@test1.com")
    void deleteStock_success() throws Exception {

        Long stockId = 42L;

        Mockito.doNothing()
                .when(stockService)
                .deleteStockForUser(Mockito.eq(stockId), Mockito.any(User.class));

        mockMvc.perform(delete("/api/stocks/{id}", stockId))
                .andExpect(status().isOk());

        Mockito.verify(stockService, Mockito.times(1))
                .deleteStockForUser(Mockito.eq(stockId), Mockito.any(User.class));
    }



}
