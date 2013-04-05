package org.drugis.addis2.controller;

import org.drugis.addis2.auth.OpenIdUserDetailsService;
import org.drugis.addis2.dao.ProjectDao;
import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/projects")
public class ProjectsController {
	@Autowired private OpenIdUserDetailsService d_userService;
	@Autowired private ProjectDao d_projectDao;

	@RequestMapping(value="", method = RequestMethod.GET)
	public String list(Model model, WebRequest request) {
		User user = d_userService.getActiveUser(request);
		model.addAttribute("projects", d_projectDao.findProjectsByOwner(user));
		return "projects/list";
	}
	
	@RequestMapping(value="", method = RequestMethod.POST)
	public String createAction(WebRequest request) {
		Project project = new Project();
		project.owner = d_userService.getActiveUser(request);
		d_projectDao.save(project);
		return "redirect:/projects/" + project.id + "/edit";
	}
	
	@RequestMapping(value="/{id}/edit", method = RequestMethod.GET)
	public String editForm(ModelMap model, @PathVariable Long id) {
		model.put("project", d_projectDao.get(id));
		return "projects/edit";
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.POST)
	public String editAction(@PathVariable Long id, Project project, BindingResult br, ModelMap model) {
		Project existing = d_projectDao.get(id);
		project.id = id;
		project.owner = existing.owner;
		d_projectDao.save(project);
		return "redirect:/projects";
	}
}