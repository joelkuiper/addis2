package org.drugis.addis2.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Joiner;

@Controller
@RequestMapping("/data")
public class TrialverseProxyController {
	@Autowired private RestTemplate d_rest;
	@Value("#{settings['trialverse.url']}") private String d_trialverse;
	
	public static List<Map<String, String>> createPaginationLinks(String baseUrl, String repositoryName, int limit, Map<String, Integer> pageInfo) {
		int currentPage = pageInfo.get("number");
		int totalPages = pageInfo.get("totalPages");
		List<Map<String, String>> links = new ArrayList<>();
		
		if (currentPage > 1) {
			Map<String, String> map = new HashMap<>();
			map.put("rel", repositoryName + ".prev");
			map.put("href", getRequestUrl(baseUrl, currentPage - 1, limit));
			links.add(map);
		}
		if (currentPage < totalPages) {
			Map<String, String> map = new HashMap<>();
			map.put("rel", repositoryName + ".next");
			map.put("href", getRequestUrl(baseUrl, currentPage + 1, limit));
			links.add(map);
		}
		return links;
	}

	private static String getRequestUrl(String baseUrl, int pageNumber, int limit) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
		builder.queryParam("page", pageNumber);
		builder.queryParam("limit", limit);
		String requestUrl = builder.build().toUriString();
		return requestUrl;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/indications")
	public @ResponseBody Object getIndications(
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="20") Integer limit,
			@RequestParam(defaultValue="") String q,
			HttpServletRequest request) {
		final String baseUrl = request.getRequestURL().toString();
		@SuppressWarnings("rawtypes")
		Map response = d_rest.getForObject(
				"{trialVerse}/concepts/search/typeAndName?type=INDICATION&page={page}&limit={limit}&q={q}",
				Map.class, d_trialverse, page, limit, q);
		response.put("links", createPaginationLinks(baseUrl, "indications", limit,
				(Map<String, Integer>) response.get("page")));
		return response;
	}
	
	@RequestMapping("/studies")
	public @ResponseBody Object getStudies(
			@RequestParam("population") final URI populationURI,
			@RequestParam("interventions") final List<URI> interventionURIs,
			@RequestParam("outcomes")  final List<URI> outcomeURIs) {
		List<UUID> interventions = new ArrayList<>();
		for(URI intervention : interventionURIs) { 
			interventions.add(getUUIDFromURI(intervention));
		}
		List<UUID> outcomes = new ArrayList<>();
		for(URI outcome : outcomeURIs) { 
			outcomes.add(getUUIDFromURI(outcome));
		}
		Joiner csv = Joiner.on(",");
		return d_rest.getForObject(
			"{trialverse}/studies/findByConcepts?indication={indication}&variables={outcomes}&treatments={interventions}",
			Object.class, d_trialverse, getUUIDFromURI(populationURI), csv.join(outcomes), csv.join(interventions));
	}
	
	private UUID getUUIDFromURI(URI conceptURI) { 
		String[] segments = conceptURI.getPath().split("/");
		return UUID.fromString(segments[segments.length-1]);
	}
	
	@RequestMapping("/{type}")
	public @ResponseBody Object getForConcept(
			final @PathVariable  String type,
			final @RequestParam("indication") URI indicationURI,
			final @RequestParam(value="q", required=false) String name) {
		return d_rest.getForObject(
				"{concept}/{type}?name={name}",
				Object.class, indicationURI, type, name);
	}
	
	
		
}