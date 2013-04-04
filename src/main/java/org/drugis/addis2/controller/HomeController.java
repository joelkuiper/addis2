package org.drugis.addis2.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.drugis.addis2.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	@Autowired private UserDao d_dao;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		model.addAttribute("serverTime", dateFormat.format(new Date()));
		model.addAttribute("users", d_dao.findUsers().toString());
		
		return "home";
	}
}
