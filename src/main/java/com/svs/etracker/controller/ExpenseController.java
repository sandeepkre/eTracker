package com.svs.etracker.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.svs.etracker.model.Category;
import com.svs.etracker.model.Expense;
import com.svs.etracker.service.CategoryService;
import com.svs.etracker.service.ExpenseService;

@Controller
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	public EntityManager entityManager;

	@Value("${address}")
	private String address;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields(new String[] { "category" });

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, "createdDate", new CustomDateEditor(format, false));
	}

	@RequestMapping(value = "/expense", method = RequestMethod.GET)
	public String hello(Model model) {

		List<String> categories = categoryService.getOnlyCategories();
		model.addAttribute("categories", categories);
		String addressAddEmployee = address+"trackEmployee";
		String addressTracker = address+"tracker";
		model.addAttribute("empaddress", addressAddEmployee);
		model.addAttribute("address", addressTracker);
		return "heloo";
	}

	@RequestMapping(value = "/addCategories", method = RequestMethod.POST)
	public String addCategories(@RequestParam("addcategory") String newCategory) {
		Category category = new Category();
		category.setCategory(newCategory);
		categoryService.addCategory(category);

		return "redirect:/expense";
	}

	@RequestMapping(value = "/deleteCategories", method = RequestMethod.POST)
	public String deleteCategory(@RequestParam("deleteCategory") String deleteCategory) {
		categoryService.deleteCategory(deleteCategory);
		return "redirect:/expense";
	}

	@RequestMapping(value = "/expenseSubmission", method = RequestMethod.POST)
	public String greetingSubmit(@Valid @ModelAttribute Expense expense, @RequestParam("category") String category,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "heloo";
		}
		else{
			int categoryId = categoryService.getCategoryId(category);

			expense.setCategoryId(categoryId);

			expenseService.addexpense(expense);
			model.addAttribute("Saved", "Expense Saved");
			return "redirect:/expense";
		}
	}
}
