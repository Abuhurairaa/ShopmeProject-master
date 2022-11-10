package com.shopme.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;

@ReadingConverter
public class CustomerRestController {

	@Autowired private CustomerService service;
	
	@PostMapping("/customer/check_unique_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		return service.isEmailUnique(id,email) ? "OK" : "Duplicated";
	}
	
}
