/**
 * 
 */
package org.drugis.addis2.controller;


import javax.servlet.http.HttpServletRequest;

import org.drugis.addis2.dao.UserDao;
import org.drugis.addis2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLoginPage(@RequestParam(value="error", required=false) boolean error, ModelMap model) {
		if (error == true) {
			model.put("error", "You have entered an invalid username or password!");
		} else {
			model.put("error", "");
		}

		return "login";
	}
	
	@Autowired private UserDao dao;
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String getRegistrationPage(HttpServletRequest request,
			@RequestParam(value="auto", required=false) boolean auto, ModelMap model) {
		if (auto) {
			String openid = (String) request.getSession().getAttribute("USER_OPENID_CREDENTIAL");
			dao.save(new User(openid));
			return "redirect:/";
		} else {
			return "login";
		}
	}
}