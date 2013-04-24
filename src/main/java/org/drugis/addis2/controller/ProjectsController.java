package org.drugis.addis2.controller;


import java.security.Principal;

import org.drugis.addis2.model.Intervention;
import org.drugis.addis2.model.Outcome;
import org.drugis.addis2.model.Population;
import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.drugis.addis2.repositories.ProjectRepository;
import org.drugis.addis2.repositories.UserRepository;
import org.drugis.addis2.service.TrialverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/projects")
public class ProjectsController {
	@Autowired private ProjectRepository d_projects;
	@Autowired private UserRepository d_users;
	@Autowired private TrialverseService d_trialverseService;

	private User getActiveUser(final Principal principal) {
		return d_users.findByOpenid(principal.getName());
	}

	private boolean userIsAuthorized (final User owner, final Principal principal) {
		if (!getActiveUser(principal).equals(owner)) {
			throw new AccessDeniedException("Unauthorized access to project");
		}
		return true;
	}


	@RequestMapping(method = RequestMethod.GET)
	public String list(final Model model, final Principal principal) {
		final User user = getActiveUser(principal);
		model.addAttribute("projects", d_projects.findByOwner(user));
		return "projects/list";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String create(final Principal principal) {
		Project project = new Project();
		project.owner = getActiveUser(principal);
		project.population = new Population();
		project = d_projects.save(project);
		return "redirect:/projects/" + project.id + "/edit";
	}


	@Transactional
	@ResponseBody
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json"})
	public Project get(final Principal principal, final ModelMap model, @PathVariable final Long id) {
		final Project project = d_projects.findOne(id);
		for (final Intervention intervention : project.interventions) {
			d_trialverseService.fetchConceptProperties(intervention);
		}
		for (final Outcome outcome : project.outcomes) {
			d_trialverseService.fetchConceptProperties(outcome);
		}
		d_trialverseService.fetchConceptProperties(project.population);
		return project;
	}

	@RequestMapping(value="/{id}", method = RequestMethod.POST, consumes = {"application/json"})
	public String edit(final Principal principal,
			@PathVariable final Long id,
			@RequestBody final Project project) {
		final Project existing = d_projects.findOne(id);
		if(userIsAuthorized(existing.owner, principal)) {
			existing.description = project.description;
			existing.shortName = project.shortName;
			existing.population.conceptUrl = project.population.conceptUrl;
			existing.interventions = project.interventions;
			existing.outcomes = project.outcomes;
			d_projects.save(existing);
		}
		return "redirect:/projects/" + id;
	}

	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, consumes = {"application/json"})
	@ResponseBody
	public String delete(final Principal principal,
			@PathVariable final Long id) {
		final Project existing = d_projects.findOne(id);
		if(userIsAuthorized(existing.owner, principal)) {
			d_projects.delete(existing);
		}
		return "redirect:/projects/";
	}

	@RequestMapping(value="/{id}/edit", method = RequestMethod.GET)
	public String editForm(final Principal principal, final ModelMap model, @PathVariable final Long id) {
		final Project project = d_projects.findOne(id);
		if(userIsAuthorized(project.owner, principal)) {
			model.addAttribute("projectName", project.shortName);
		}
		return "projects/edit";
	}

	@RequestMapping(value="/{id}/studies", method = RequestMethod.GET, produces =  {"text/html"})
	public String studiesForm(final Principal principal, final ModelMap model, @PathVariable final Long id) {
		final Project project = d_projects.findOne(id);
		if(userIsAuthorized(project.owner, principal)) {
			model.addAttribute("projectName", project.shortName);
		}
		return "projects/studies";
	}

}