package BudgetPlanner.BudgetPlanner;

import BudgetPlanner.BudgetPlanner.Modell.SavingsGoal;
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
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class BudgetPlannerApplicationTests {

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


	@BeforeEach
	void setUp() {
		stockRepository.deleteAll();
		userRepository.deleteAll();

		user = new User();
		user.setName("TestUser");
		user.setEmail("test@test.com");
		user.setPassword(passwordEncoder.encode("test"));
		userRepository.save(user);

		when(userRepository.findByEmail("test@test.com"))
				.thenReturn(Optional.of(user));
	}


	@Test
	@WithMockUser(username = "test@test.com")
	void getBudgetTest() throws Exception {

		String json = """
				{
				  "user": {
				    "id": %d
				  },
				  "expenses": [
				    {"name": "Einkauf", "amount": 100.0},
				    {"name": "Essen", "amount": 20.0}
				  ]
				}
				""".formatted(user.getId());

		mockMvc.perform(post("/budgets")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.expenses.length()").value(2))
				.andExpect(jsonPath("$.expenses[0].name").value("Einkauf"))
				.andExpect(jsonPath("$.expenses[1].amount").value(20.0));
	}


	@Test
	@WithMockUser(username = "test@test.com")
	void clearBudgetsTest() throws Exception {

		String json = """
				{
				  "user": {
				    "id": %d
				  },
				  "expenses": [
				    {"name": "Einkauf", "amount": 100.0},
				    {"name": "Essen", "amount": 20.0}
				  ]
				}
				""".formatted(user.getId());

		mockMvc.perform(post("/budgets")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/budgets")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("Alle Budgets gelöscht!"));

		mockMvc.perform(get("/budgets")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}


	@Test
	@WithMockUser(username = "test@test.com")
	void getIncomeTest() throws Exception {
		String json = """
				{
				  "user": {
				    "id": %d
				  },
				  "income": [
				    {"name": "Gehalt", "amount": 2000.0}
				  ]
				}
				""".formatted(user.getId());

		mockMvc.perform(post("/income")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

		mockMvc.perform(get("/income")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.income[0].amount").value(2000.0));



	}

	@Test
	@WithMockUser(username = "test@test.com")
	void clearIncomeTest() throws Exception {

		String json = """
				{
				  "user": {
				    "id": %d
				  },
				  "income": [
				    {"name": "Gehalt", "amount": 2000.0}
				  ]
				}
				""".formatted(user.getId());

		mockMvc.perform(post("/income")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/income")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get("/income")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));

	}

	@Test
	@WithMockUser(username = "test@test.com")
	void updateGoalTest() throws Exception {

		SavingsGoal updated = new SavingsGoal();
		updated.setTitle("Neues Auto");
		updated.setTargetAmount(12000);
		updated.setCurrentAmount(2000);
		updated.setDeadline(LocalDate.of(2026, 6, 1));

		Mockito.when(savingsGoalService.update(Mockito.eq(1L), Mockito.any(), Mockito.eq(user)))
				.thenReturn(updated);

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

	@Test
	@WithMockUser(username = "test@test.com")
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

		Mockito.when(reportService.buildReport(user, "2025-01"))
				.thenReturn(response);

		mockMvc.perform(get("/api/report")
						.param("month", "2025-01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.month").value("2025-01"))
				.andExpect(jsonPath("$.incomeSum").value(3000.0))
				.andExpect(jsonPath("$.expenseSum").value(1200.0))
				.andExpect(jsonPath("$.rows.length()").value(2))
				.andExpect(jsonPath("$.rows[0].category").value("FOOD"))
				.andExpect(jsonPath("$.rows[0].budget").value(500.0))
				.andExpect(jsonPath("$.rows[0].spent").value(450.0));
	}




}



