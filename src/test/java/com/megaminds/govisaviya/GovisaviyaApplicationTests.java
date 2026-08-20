package com.megaminds.govisaviya;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GovisaviyaApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
		assertNotNull(dataSource);
	}

	@Test
	void testDatabaseConnectionAndData() throws Exception {
		System.out.println("==================================================");
		System.out.println(">>> TESTING DATABASE CONNECTION FROM BACKEND <<<");

		try (Connection connection = dataSource.getConnection()) {
			System.out.println("[SUCCESS] Connected to: " + connection.getMetaData().getURL());
			System.out.println("[SUCCESS] Database Product: " + connection.getMetaData().getDatabaseProductName() + " " + connection.getMetaData().getDatabaseProductVersion());
			System.out.println("[SUCCESS] Database User: " + connection.getMetaData().getUserName());
			assertTrue(connection.isValid(5));
		}

		List<String> tables = jdbcTemplate.queryForList(
				"SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name",
				String.class
		);
		System.out.println("--------------------------------------------------");
		System.out.println("[SUCCESS] Found " + tables.size() + " tables in database:");
		tables.forEach(t -> System.out.println("  - " + t));

		List<Map<String, Object>> roles = jdbcTemplate.queryForList("SELECT id, name FROM roles ORDER BY id");
		System.out.println("--------------------------------------------------");
		System.out.println("[SUCCESS] Roles data in database (" + roles.size() + " records):");
		roles.forEach(r -> System.out.println("  Role ID: " + r.get("id") + ", Name: " + r.get("name")));

		List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, full_name, email, user_type FROM users ORDER BY id");
		System.out.println("--------------------------------------------------");
		System.out.println("[SUCCESS] Users data in database (" + users.size() + " records):");
		users.forEach(u -> System.out.println("  User ID: " + u.get("id") + ", Type: " + u.get("user_type") + ", Name: " + u.get("full_name") + " (" + u.get("email") + ")"));

		List<Map<String, Object>> products = jdbcTemplate.queryForList("SELECT id, name, category, price_per_unit, available_quantity, location FROM products ORDER BY id");
		System.out.println("--------------------------------------------------");
		System.out.println("[SUCCESS] Products in database (" + products.size() + " records):");
		products.forEach(p -> System.out.println("  Product: " + p.get("name") + " [" + p.get("category") + "] - Rs. " + p.get("price_per_unit") + " (Qty: " + p.get("available_quantity") + " kg, " + p.get("location") + ")"));
		System.out.println("==================================================");
	}

}
