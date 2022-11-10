package com.shopme.admin.product;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProductRestController {
	
	@Autowired private ProductService service;
	
	@PostMapping("/products/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
		
		return service.checkUnique(id, name);
	}
	
//	@GetMapping("/products/{id}/categories")
//	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException{
//		List<CategoryDTO> listCategories = new ArrayList<>();
//		
//		try {
//			Brand brand = service.get(brandId);
//			Set<Category> categories = brand.getCategories();
//			
//			for(Category category : categories) {
//				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
//				listCategories.add(dto);
//			}
//			
//			return listCategories;
//			
//		} catch (BrandNotFoundException e) {
//			throw new BrandNotFoundRestException();
//		}
//		
//		
//	}

}
