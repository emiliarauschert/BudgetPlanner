package BudgetPlanner.BudgetPlanner;

import BudgetPlanner.BudgetPlanner.Modell.Budget;
import BudgetPlanner.BudgetPlanner.Modell.Expense;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BudgetPlannerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@Test
	void contextLoads() {
	}

	@Test
	void register() throws Exception {
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
	void getBudget() throws Exception {
		User user = new User();
		user.setName("TestUser");
		user.setEmail("test@test.com");
		userRepository.save(user);

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

}


