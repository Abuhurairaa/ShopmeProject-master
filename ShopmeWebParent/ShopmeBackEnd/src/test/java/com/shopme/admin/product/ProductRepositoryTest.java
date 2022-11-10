package com.shopme.admin.product;


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

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);
		
		Product product = new Product();
		product.setName("Acer Aspire Desktop");
		product.setAlias("acer_aspire_desktop");
		product.setShortDescription("short description for Acer Aspire Desktop");
		product.setFullDescription("full description for Acer Aspire Desktop");
		
		product.setCategory(category);
		product.setBrand(brand);
		
		product.setPrice(400);
		product.setCost(360);
		product.setEnabled(true);
		product.setInStock(true);
		
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllProduct() {
		Iterable<Product> iterableProduct = repo.findAll();
		
		iterableProduct.forEach(System.out::println);
	}
	
	@Test
	public void testGetProduct() {
		Integer id = 2;
		Product product = repo.findById(id).get();
		
		System.out.println(product);
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		product.setPrice(499);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		assertThat(updatedProduct.getPrice()).isEqualTo(499);
		
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test 
	public void testSaveProductWithImage() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image 1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra-image3.png");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
		
	}
	
	@Test 
	public void testSaveProductWithDetails() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.addDetail("Device Memory", "128GB");
		product.addDetail("CPU Model", "MediaTek");
		product.addDetail("OS", "Andoid");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getDetails()).isNotEmpty();
		
	}
	

}
