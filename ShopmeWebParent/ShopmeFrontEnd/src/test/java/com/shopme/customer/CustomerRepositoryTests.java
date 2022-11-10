package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {
	
	@Autowired private CustomerRepository repo;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testCreateCustomer1() {
		Integer countryId = 165; //Nigeria
		Country country = entityManager.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("Abu");
		customer.setLastName("Huraira");
		customer.setEmail("abu@gmail.com");
		customer.setPassword("123456");
		customer.setPhoneNumber("54512565");
		customer.setAddressLine1("Flic en flac");
		customer.setPostalCode("90012");
		customer.setCity("Flic");
		customer.setState("Black River");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateCustomer2() {
		Integer countryId = 106; //India
		Country country = entityManager.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("sanya");
		customer.setLastName("ladu");
		customer.setPassword("123456");
		customer.setEmail("abu@gmail.com");
		customer.setPhoneNumber("54512565");
		customer.setAddressLine1("Flic en flac");
//		customer.setAddressLine2("kubwa abuja");
		customer.setCity("Flic");
		customer.setState("Black River");
		customer.setPostalCode("90012");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
		
	}
	
	@Test 
	public void testListCustomer() {
		Iterable<Customer> customers = repo.findAll();
		
		customers.forEach(System.out::println);
		
		assertThat(customers).hasSizeGreaterThan(1);
	}
	
	@Test 
	public void testUpdateCustomer() {
		Integer customerId = 1;
		String lastName = "last Name";
		
		Customer customer = repo.findById(customerId).get();
		customer.setLastName(lastName);
		customer.setEnabled(true);
		
		Customer updatedCustomer = repo.save(customer);
		assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
		
	}
	
	@Test 
	public void testGetCustomer() {
		Integer customerId = 2;
		Optional<Customer> findById = repo.findById(customerId);
		
		assertThat(findById).isPresent();
		Customer customer = findById.get();
		
		System.out.println(customer);
	}
	
	@Test 
	public void testDeleteCustomer() {
		Integer customerId = 2;
		repo.deleteById(customerId);
		
		Optional<Customer> findById = repo.findById(customerId);
		assertThat(findById).isNotPresent();	
	}
	
	@Test 
	public void testFindByEmail() {
		String email = "abu@gmail.com";
		Customer customer = repo.findByEmail(email);
		
		assertThat(customer).isNotNull();
		System.out.println(customer);
	}
	
	@Test 
	public void testFindByVerificationCode() {
		String code = "code_123";
		Customer customer = repo.findByVerificationCode(code);
		
		assertThat(customer).isNotNull();
		System.out.println(customer);
	}
	
	@Test 
	public void testEnabledCustomer() {
		Integer customerId = 1;
		repo.enable(customerId);
		
		Customer customer = repo.findById(customerId).get();
		assertThat(customer.isEnabled()).isTrue();
		
	}
	

}
