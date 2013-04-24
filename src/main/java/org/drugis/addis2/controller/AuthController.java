package org.drugis.addis2.controller;

import javax.servlet.http.HttpServletRequest;

import org.drugis.addis2.model.User;
import org.drugis.addis2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired private UserRepository d_users;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLoginPage(
			@RequestParam(value="error", required=false)
			final boolean error,
			final ModelMap model) {
		if (error == true) {
			model.put("error", "You have entered an invalid username or password!");
		} else {
			model.put("error", "");
		}
		return "login";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String getRegistrationPage(
			final HttpServletRequest request,
			@RequestParam(value="auto", required=false) final boolean auto,
			final ModelMap model) {
		if (auto) {
			final String openid = (String) request.getSession().getAttribute("USER_OPENID_CREDENTIAL");
			d_users.save(new User(openid));
			return "redirect:/";
		} else {
			return "login";
		}
	}
}
