package org.drugis.addis2.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/data")
public class TrialverseProxyController {
	@Autowired private RestTemplate d_rest;
	
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

	private @Value("#{settings['trialverse.url']}") String d_trialVerse;

	@SuppressWarnings("unchecked")
	@RequestMapping("/indications")
	public @ResponseBody Object getIndications(
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="20") Integer limit,
			@RequestParam(defaultValue="") String q,
			HttpServletRequest request) {
		final String baseUrl = request.getRequestURL().toString();
		@SuppressWarnings("rawtypes")
		Map object = d_rest.getForObject(
				"{trialVerse}/concepts/search/typeAndName?type=INDICATION&page={page}&limit={limit}&q={q}",
				Map.class, d_trialVerse, page, limit, q);
		object.put("links", createPaginationLinks(baseUrl, "indications", limit,
				(Map<String, Integer>) object.get("page")));
		return object;
	}
	
	@RequestMapping("/treatments")
	public @ResponseBody Object getTreatments(
			@RequestParam String indication) {
		return d_rest.getForObject(
				"{concept}/treatments",
				Object.class, indication);
	}
}