package com.shopme.admin.product;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUplaodUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired private ProductService productService;
	@Autowired private BrandService brandService;
	@Autowired private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		
		return listByPage(1, model, "name", "asc", null, 0);
				
//		List<Product> listProducts =	productService.listAll();
//		model.addAttribute("listProducts", listProducts);	
//		return "/products/products";
	}
	
	@GetMapping("/products/page/{pageNumber}")
	public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword, @Param("categoryId") Integer categoryId
			) {
		
//		System.out.println("Selected cat ID: "+ categoryId);
		
		Page<Product> page = productService.listByPage(pageNumber,sortField,sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		
		long startCount = (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE +1;
		long endCount = pageNumber + ProductService.PRODUCTS_PER_PAGE -1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reversSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reversSortDir", reversSortDir);
		model.addAttribute("keyword", keyword);
		
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);
		
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser
			) throws IOException {
		
		if(loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			redirectAttributes.addFlashAttribute("message", "The product has been saved successfully!");		
			return "redirect:/products";
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);	
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);
			
		Product savedProduct = productService.save(product);
		
		ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		
		ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);
				
		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully!");
		
		return "redirect:/products";
	}
	

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product Id: " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		
		try {
			
			productService.delete(id);
			
			String productExtraImagesDir = "../product-images/" + id  + "/extras";
			String productImagesDir = "../product-images/" + id;
			
			FileUplaodUtil.removeDir(productExtraImagesDir);		
			FileUplaodUtil.removeDir(productImagesDir);
			
			model.addAttribute("message", "The Product Id: " + id + "has been deleted Successfully!");
			
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes rAttributes) {
		
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (Id: "+id+")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			
			
			return "products/product_form";
			
		} catch (ProductNotFoundException e) {
			rAttributes.addFlashAttribute("message", e.getMessage());
			return "rediect:/products";
		}	
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewlProductDtails(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes rAttributes) {
		
		try {
			Product product = productService.get(id);
			model.addAttribute("product", product);
			
			
			return "products/product_detail_modal";
			
		} catch (ProductNotFoundException e) {
			rAttributes.addFlashAttribute("message", e.getMessage());
			return "rediect:/products";
		}	
	}
	
}
