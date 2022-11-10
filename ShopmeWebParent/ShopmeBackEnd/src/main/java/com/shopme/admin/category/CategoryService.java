package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository repo;

	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNumber, String sortDir,
			String keyword) {
		Sort sort = Sort.by("name");

		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}

		Pageable pageable = PageRequest.of(pageNumber - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		
		Page<Category> pageCategories = null;
		
		if (keyword != null && !keyword.isBlank()) {
			pageCategories = repo.serach(keyword, pageable);
		}else {
			pageCategories = repo.findRootCategories(pageable);
		}

		List<Category> rooCategories = pageCategories.getContent();

		pageInfo.setTotalElements( pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		if (keyword != null && !keyword.isBlank()) {
			List<Category> searchResult = pageCategories.getContent();
			
			for(Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			
			return searchResult;
			
		}else {
			return listHierarchicalCategories(rooCategories, sortDir);
		}
		
	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));

			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();

				hierarchicalCategories.add(Category.copyFull(subCategory, name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel,
			String sortDir) {

		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);

		for (Category subCategory : children) {

			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}

			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}

	}

	public Category save(Category category) {
		Category parent = category.getParent();
		if(parent != null) {
			String allParentIDs = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIDs += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIDs);
		}
		
		return repo.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {

		List<Category> categoriesUsedInForm = new ArrayList<>();

		Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());

		for (Category category : categoriesInDB) {
			if (category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));

				Set<Category> children = sortSubCategories(category.getChildren());
				for (Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
				}
			}
		}

		return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {

		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());

		for (Category subCategory : children) {

			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}

			name += subCategory.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any Category with ID: " + id);
		}

	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean iscreatingNew = (id == null || id == 0);

		Category categoryByName = repo.findByName(name);

		if (iscreatingNew) {
			if (categoryByName != null) {
				return "Duplicate Name";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "Duplicate Alias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "Duplicate Name";
			}

			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "Duplicate Alias";
			}
		}

		return "OK";
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children) {

		return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});

		sortedChildren.addAll(children);
		return sortedChildren;
	}

	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);

		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Counld not find any Category with Id: " + id);
		}

		repo.deleteById(id);
	}

}
