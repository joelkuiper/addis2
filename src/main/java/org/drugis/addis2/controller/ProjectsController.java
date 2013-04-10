package org.drugis.addis2.controller;


import org.drugis.addis2.auth.OpenIdUserDetailsService;
import org.drugis.addis2.dao.ProjectDao;
import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/projects")
public class ProjectsController {
	@Autowired private OpenIdUserDetailsService d_userService;
	@Autowired private ProjectDao d_projectDao;
	
	
	private boolean userIsAuthorized (User owner, WebRequest request) { 
		if (!d_userService.getActiveUser(request).equals(owner)) {
			throw new AccessDeniedException("Unauthorized access to project");
		}
		return true;
	}
	
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
		project = d_projectDao.save(project);
		return "redirect:/projects/" + project.id + "/edit";
	}
	
	@RequestMapping(value="/{id}/edit", method = RequestMethod.GET)
	public String editForm(WebRequest request, ModelMap model, @PathVariable Long id) {
		Project project = d_projectDao.get(id);
		if(userIsAuthorized(project.owner, request)) { 
			model.addAttribute("project", project);
		}
		return "projects/edit";
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.POST)
	public String editAction(WebRequest request,
			@PathVariable Long id, 
			Project project, 
			ModelMap model) {
		Project existing = d_projectDao.get(id);
		if(userIsAuthorized(existing.owner, request)) { 
			project.id = id;
			project.owner = existing.owner;
			d_projectDao.save(project);
		}  
		return "redirect:/projects";
	}
}