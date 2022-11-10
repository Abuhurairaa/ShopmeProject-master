package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.shopme.admin.FileUplaodUtil;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listFirsrtPage(Model model) {
		return listByPage(1, model, "email", "asc", null);
		
//		List<User> listUsers = service.listAll();
//		model.addAttribute("listUsers",listUsers);
//		return "users";
	}
	
	@GetMapping("/users/page/{pageNumber}")
	public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword
			) {
		Page<User> page = service.listByPage(pageNumber,sortField,sortDir, keyword);
		List<User> listUsers = page.getContent();
		
//		System.out.println("pageNumber "+ pageNumber);
//		System.out.println("Page Element "+ page.getTotalElements());
//		System.out.println("Page Total "+ page.getTotalPages());
		
		long startCount = (pageNumber - 1) * UserService.USERS_PER_PAGE +1;
		long endCount = pageNumber + UserService.USERS_PER_PAGE -1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reversSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reversSortDir", reversSortDir);
		model.addAttribute("keyword", keyword);
		
		return "users/users";
	}
	
	@GetMapping("/users/new")
	public String createNewUser(Model model) {
		List<Role> listRoles = service.listRoles();
		User user = new User();
		user.setEnabled(true);
		
		model.addAttribute("user",user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		//System.out.println(user);
		//System.out.println(multipartFile.getOriginalFilename());
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			
			String upladDir = "user-photos/"+ savedUser.getId();
			
			FileUplaodUtil.cleanDir(upladDir);
			FileUplaodUtil.saveFile(upladDir, fileName, multipartFile);
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "The User has been saved Successfully!");
		
		return getRedirectURLtoAffectedUser(user);
	}

	private String getRedirectURLtoAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
		 public String editUser(@PathVariable(name = "id") Integer id, 
				 Model model,
				 RedirectAttributes redirectAttributes) {
			try {
				User user = service.get(id);
				List<Role> listRoles = service.listRoles();
				model.addAttribute("user",user);
				model.addAttribute("listRoles", listRoles);
				model.addAttribute("pageTitle", "Edit User (ID: "+ id +")");
				
				return "users/user_form";
			} catch (UserNotFoundException e) {
				redirectAttributes.addFlashAttribute("message", e.getMessage());
//				e.printStackTrace();
				return "redirect:/users";
			}
			
		 }
	
	@GetMapping("/users/delete/{id}")
	 public String deleteUser(@PathVariable(name = "id") Integer id, 
			 Model model,
			 RedirectAttributes redirectAttributes) {
		
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The User ID "+id+" has been deleted successfully!");

		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
//			e.printStackTrace();
		}
		return "redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id")Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes
			) {
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled": "disabled";
		String message = "The User ID "+ id+" has been "+ status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
		
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, response);
		
	}
	

}
