package BudgetPlanner.BudgetPlanner;

import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

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
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();

		User user = new User();
		user.setName("TestUser");
		user.setEmail("test@test.com");
		userRepository.save(user);
	}


	@Test
	void contextLoads() {
	}

	@Test
	void registerTest() throws Exception {
		String json = """
				{
				  "name": "test",
				  "email": "test@test.com",
				  "password": "test"
				}
				""";
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

	}

	@Test
	void loginTest() throws Exception {

	}

	@Test
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
	void clearIncomeTest() throws Exception {}

	@Test
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
	void hashPasswordTest() throws Exception {

	}

	@Test
	void getStockTest() throws Exception {

	}

	@Test
	void getStockQuote() throws Exception {

	}



}



