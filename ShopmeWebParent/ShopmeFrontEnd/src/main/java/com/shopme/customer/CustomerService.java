package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {
	
	@Autowired private CountryRepository countryRepo;
	@Autowired private CustomerRepository customerRepo;
	@Autowired PasswordEncoder passwordEncoder;
	
	
	public List<Country> listAllCountries(){
	 return	countryRepo.findAllByOrderByNameAsc();	
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer customer = customerRepo.findByEmail(email);
		
		if (customer == null)
			return true;

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (customer != null) {
				return false;
			}
		} else {
			if (customer.getId() != id) {
				return false;
			}
		}

		return true;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepo.save(customer);
//		System.out.println("Verification code: "+ randomCode);
	}

	private void encodePassword(Customer customer) {
		
		String encodePassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodePassword);
		
	}
	
	public boolean verify(String verificationCode) {
		Customer customer = customerRepo.findByVerificationCode(verificationCode);
		
		if(customer == null || customer.isEnabled()) {
			return false;
		}else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}

}
