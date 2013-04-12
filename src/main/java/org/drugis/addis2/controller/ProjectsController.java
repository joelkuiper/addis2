package org.drugis.addis2.controller;


import java.security.Principal;

import org.drugis.addis2.model.Population;
import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.drugis.addis2.repositories.ProjectRepository;
import org.drugis.addis2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/projects")
public class ProjectsController {
	@Autowired private ProjectRepository d_projects;
	@Autowired private UserRepository d_users;

	private User getActiveUser(Principal principal) { 
		return d_users.findByOpenid(principal.getName());
	}
	
	private boolean userIsAuthorized (User owner, Principal principal) { 
		if (!getActiveUser(principal).equals(owner)) {
			throw new AccessDeniedException("Unauthorized access to project");
		}
		return true;
	}
	
	@RequestMapping(value="", method = RequestMethod.GET)
	public String list(Model model, Principal principal) {
		User user = getActiveUser(principal);
		model.addAttribute("projects", d_projects.findByOwner(user));
		return "projects/list";
	}
	
	@RequestMapping(value="", method = RequestMethod.POST)
	public String createAction(Principal principal) {
		Project project = new Project();
		project.owner = getActiveUser(principal);
		project.population = new Population();
		project = d_projects.save(project);
		return "redirect:/projects/" + project.id + "/edit";
	}
	
	@RequestMapping(value="/{id}/edit", method = RequestMethod.GET)
	public String editForm(Principal principal, ModelMap model, @PathVariable Long id) {
		Project project = d_projects.findOne(id);
		if(userIsAuthorized(project.owner, principal)) { 
			model.addAttribute("project", project);
		}
		return "projects/edit";
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.POST)
	public String editAction(Principal principal,
			@PathVariable Long id, 
			Project project, 
			ModelMap model) {
		Project existing = d_projects.findOne(id);
		if(userIsAuthorized(existing.owner, principal)) { 
			existing.description = project.description;
			existing.shortName = project.shortName;
			existing.population.indicationConceptUrl = project.population.indicationConceptUrl;
			d_projects.save(existing);
		}
		return "redirect:/projects";
	}
}