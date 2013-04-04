package org.drugis.addis2.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.drugis.addis2.dao.ProjectDao;
import org.drugis.addis2.dao.UserDao;
import org.drugis.addis2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/projects")
public class ProjectsController {
	@Autowired private UserDao d_userDao;
	@Autowired private ProjectDao d_projectDao;

	@RequestMapping(value="", method = RequestMethod.GET)
	public String home(Locale locale, Model model, Principal principal) {
		String activeUser = ((org.springframework.security.core.userdetails.User)((Authentication) principal).getPrincipal()).getUsername();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		model.addAttribute("serverTime", dateFormat.format(new Date()));
		User user = d_userDao.findUserByOpenId(activeUser).iterator().next();
		model.addAttribute("projects", d_projectDao.findProjectsByOwner(user).toString());
		
		return "projects";
	}
}
