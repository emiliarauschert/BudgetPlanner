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
				  "user": {
				    "id": %d
				  },
				  "expenses": [
				    {"name": "Einkauf", "amount": 100.0},
				    {"name": "Essen", "amount": 20.0}
				  ]
				}
				""".formatted(user.getId());

		String response = mockMvc.perform(post("/api/budgets")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andReturn()
				.getResponse()
				.getContentAsString();

		Long id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

		mockMvc.perform(delete("/api/budgets/" + id))
				.andExpect(status().isOk());
	}


	@Test
	@WithMockUser(username = "test@test1.com")
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

		mockMvc.perform(post("/api/income")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/income")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].amount").value(2000.0));



	}

	@Test
	@WithMockUser(username = "test@test1.com")
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

		mockMvc.perform(post("/api/income")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/api/income")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/income")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));

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
				reportService.buildReport(Mockito.any(User.class), Mockito.eq("2025-01"))
		).thenReturn(response);


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



