package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 10;
	public static final int SEARCH_RESULT_PER_PAGE = 10;
	
	@Autowired private ProductRepository repo;
	
	public Page<Product> listByCategory(int pageNumber, Integer categoryId){
		String categoryIDMatch = "-" + String.valueOf(categoryId) + "-";
		Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCT_PER_PAGE);
		
		return repo.listByCategory(categoryId, categoryIDMatch, pageable);	
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		
		if(product == null) {
			throw new ProductNotFoundException("Could not find any product with alias " + alias);
		}
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNumber){
		Pageable pageable = PageRequest.of(pageNumber - 1, SEARCH_RESULT_PER_PAGE);
		return repo.search(keyword, pageable);
	}
	
}
