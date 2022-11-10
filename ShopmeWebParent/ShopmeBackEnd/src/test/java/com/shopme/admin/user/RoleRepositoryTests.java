package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	
	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "Managed Everything");
		Role savedRole= repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestRoles() {
		Role roleSalesPerson = new Role("SalesPerson", "Managed Products Price,"
				+" Customers, Shipping, Orders and Sales Report");
		Role roleEditor = new Role("Editor", "Managed Categories, Brands,"
				+" Products,Articles and Menus");
		Role roleShipper = new Role("Shipper", "View Products, View Orders"
				+" and Update Orders Status");
		Role roleAssistant = new Role("Assistant", "Managed Questions and Reviews");
		
		repo.saveAll(List.of(roleSalesPerson,roleEditor,roleShipper,roleAssistant));
	}
}
