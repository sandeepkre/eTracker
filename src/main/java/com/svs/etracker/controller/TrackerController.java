package com.svs.etracker.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.svs.etracker.service.CategoryService;
import com.svs.etracker.service.ExpenseService;
import com.svs.etracker.util.WriteExcel;

@Controller
public class TrackerController {

	@Autowired
	private WriteExcel writeExcel;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private CategoryService categoryService;

	@Value("${excelFilePath}")
	private String excelFilePath;



	@RequestMapping(value = "/tracker", method = RequestMethod.GET)
	public String trackerPage(Model model) {

		List<String> categories = categoryService.getOnlyCategories();
		model.addAttribute("categories", categories);
		return "tracker";
	}

	@RequestMapping(value = "/dateTracker", method = RequestMethod.POST)
	public String trackViaDate(Model model, @RequestParam("fromDate") String fromDate,
			@RequestParam("tillDate") String tillDate) throws ParseException, IOException {

		double total = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date fDate = format.parse(fromDate);
		Date tDate = format.parse(tillDate);
		List<Object[]> object = expenseService.getbydate(fDate, tDate);



		for (int i=0; i<object.size(); i++){
			Object[] row = object.get(i);
			double one = (double) row[1];
			total = total +one;
		}

		//String excelFilePath = "C:/Users/Balaji/Desktop/Download/excel.xls";
		writeExcel.writeExcel(object, excelFilePath);
		System.out.println("Success!!!!!!");


		model.addAttribute("total", total);
		model.addAttribute("from", fDate);
		model.addAttribute("to", tDate);
		model.addAttribute("expense", object);
		return "dataFromDate";
	}

	@RequestMapping(value = "/categoryTracker", method = RequestMethod.POST)
	public String trackViaCategory(Model model, @RequestParam("trackCategory") String category) throws IOException {
		double total = 0;
		List<Object[]> expenses = expenseService.getbyCategory(category);

		for (int i=0; i<expenses.size(); i++){
			Object[] row = expenses.get(i);
			double one = (double) row[1];
			total = total +one;
		}



		//String excelFilePath = "C:/Users/Balaji/Desktop/Download/excel.xls";
		writeExcel.writeExcel(expenses, excelFilePath);
		System.out.println("Success!!!!!!");


		model.addAttribute("total", total);
		model.addAttribute("category", category);
		model.addAttribute("expense", expenses);
		return "dataFromCategory";
	}

	@RequestMapping(value = "/dateAndCategoryTracker", method = RequestMethod.POST)
	public String trackViaDateAndCategory(Model model, @RequestParam("trackCategory") String category,
			@RequestParam("fromDate") String fromDate, @RequestParam("tillDate") String tillDate)
					throws ParseException, IOException {
		double total = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date fDate = format.parse(fromDate);
		Date tDate = format.parse(tillDate);
		List<Object[]> cdexpense = expenseService.getByCategoryAndDate(fDate, tDate, category);
		for (int i=0; i<cdexpense.size(); i++){
			Object[] row = cdexpense.get(i);
			double one = (double) row[1];
			total = total +one;
		}

		//String excelFilePath = "C:/Users/Balaji/Desktop/Download/excel.xls";
		writeExcel.writeExcel(cdexpense, excelFilePath);
		System.out.println("Success!!!!!!");

		model.addAttribute("expense", cdexpense);
		model.addAttribute("total", total);
		model.addAttribute("from", fDate);
		model.addAttribute("to", tDate);
		model.addAttribute("category", category);
		return "DataFromCategoryAndDate";
	}

	@RequestMapping(value = "/deleteExpenseFromCategory", method = RequestMethod.POST)
	public String deleteExpenseCategory(Model model, @RequestParam("checkboxgroup") String[] expenseIds) {
		int[] result = new int[expenseIds.length];
		for (int i = 0; i < expenseIds.length; i++) {
			result[i] = Integer.parseInt(expenseIds[i]);
		}
		expenseService.deleteExpenses(result);

		return "redirect:/tracker";
	}

	@RequestMapping(value = "/deleteExpenseFromDate", method = RequestMethod.POST)
	public String deleteExpenseDate(Model model, @RequestParam("checkboxgroup") String[] expenseIds) {
		int[] result = new int[expenseIds.length];
		for (int i = 0; i < expenseIds.length; i++) {
			result[i] = Integer.parseInt(expenseIds[i]);
		}
		expenseService.deleteExpenses(result);
		return "redirect:/tracker";
	}

	@RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	public void getFile(HttpServletResponse response) {
		try {
			// get your file as InputStream
			InputStream initialFile = new FileInputStream(excelFilePath);
			response.setHeader("Content-disposition", "attachment;filename=myExcel.xls");
			response.setContentType("application/vnd.ms-excel");
			// copy it to response's OutputStream
			org.apache.commons.io.IOUtils.copy(initialFile, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException ex) {

			throw new RuntimeException("IOError writing file to output stream");
		}

	}

}
