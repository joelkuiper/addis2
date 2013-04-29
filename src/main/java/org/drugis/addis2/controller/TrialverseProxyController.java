package org.drugis.addis2.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Controller
@RequestMapping("/data")
public class TrialverseProxyController {
	@Autowired private RestTemplate d_rest;
	@Value("#{settings['trialverse.url']}") private String d_trialverse;

	public static List<Map<String, String>> createPaginationLinks(
			final String baseUrl,
			final String repositoryName,
			final int limit,
			final Map<String, Integer> pageInfo) {
		final int currentPage = pageInfo.get("number");
		final int totalPages = pageInfo.get("totalPages");
		final List<Map<String, String>> links = new ArrayList<>();

		if (currentPage > 1) {
			final Map<String, String> map = new HashMap<>();
			map.put("rel", repositoryName + ".prev");
			map.put("href", getRequestUrl(baseUrl, currentPage - 1, limit));
			links.add(map);
		}
		if (currentPage < totalPages) {
			final Map<String, String> map = new HashMap<>();
			map.put("rel", repositoryName + ".next");
			map.put("href", getRequestUrl(baseUrl, currentPage + 1, limit));
			links.add(map);
		}
		return links;
	}

	private static String getRequestUrl(final String baseUrl, final int pageNumber, final int limit) {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
		builder.queryParam("page", pageNumber);
		builder.queryParam("limit", limit);
		final String requestUrl = builder.build().toUriString();
		return requestUrl;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/indications")
	public @ResponseBody Object getIndications(
			@RequestParam(defaultValue="1") final Integer page,
			@RequestParam(defaultValue="20") final Integer limit,
			@RequestParam(defaultValue="") final String q,
			final HttpServletRequest request) {
		final String baseUrl = request.getRequestURL().toString();
		@SuppressWarnings("rawtypes")
		final
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
		final List<UUID> interventions = new ArrayList<>();
		for(final URI intervention : interventionURIs) {
			interventions.add(getUUIDFromURI(intervention));
		}
		final List<UUID> outcomes = new ArrayList<>();
		for(final URI outcome : outcomeURIs) {
			outcomes.add(getUUIDFromURI(outcome));
		}
		final Joiner csv = Joiner.on(",");
		return d_rest.getForObject(
			"{trialverse}/studies/findByConcepts?indication={indication}&variables={outcomes}&treatments={interventions}",
			Object.class, d_trialverse, getUUIDFromURI(populationURI), csv.join(outcomes), csv.join(interventions));
	}

	private UUID getUUIDFromURI(final String conceptURI) {
		try {
			return getUUIDFromURI(new URI(conceptURI));
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private UUID getUUIDFromURI(final URI conceptURI) {
		final String[] segments = conceptURI.getPath().split("/");
		return UUID.fromString(segments[segments.length - 1]);
	}

	@RequestMapping("/measurements")
	@SuppressWarnings("unchecked")
	public @ResponseBody Object getMeasurements(
			@RequestParam("population") final URI populationURI,
			@RequestParam("interventions") final List<URI> interventionURIs,
			@RequestParam("outcome")  final URI outcomeURI) {
		final List<Map<String, Object>> studies = (List<Map<String, Object>>) getStudies(
				populationURI, interventionURIs, Collections.singletonList(outcomeURI));

		final List<Long> studyIds = new ArrayList<>();
		for(final Map<String, Object> study : studies)  {
			studyIds.add(longValue(study.get("id")));
		}

		final Joiner csv = Joiner.on(",");
		final List<Map<String, Object>> mappings = (List<Map<String, Object>>) d_rest.getForObject(
			"{trialverse}/studies/findVariableMappings?variable={variable}&studies={studies}",
			Object.class, d_trialverse, getUUIDFromURI(outcomeURI), csv.join(studyIds));
		final Map<Long, String> studyVariables = new HashMap<>();
		for (final Map<String, Object> mapping : mappings) {
			studyVariables.put(longValue(mapping.get("studyId")), (String)mapping.get("concept"));
		}

		final List<Map<String, Object>> allMeasurements = new ArrayList<>();
		for (final Map<String, Object> study : studies) {
			final Long studyId = longValue(study.get("id"));

			// Get the study-local variable concept
			if (!studyVariables.containsKey(studyId)) {
				continue;
			}
			final String variable = studyVariables.get(studyId);

			// Get the one true measurement moment (will need to be user-selectable at some future point)
			final List<Map<String, Object>> mms = (List<Map<String, Object>>) study.get("measurementMoments");
			final Collection<Map<String, Object>> filtered = Collections2.filter(mms,
					new Predicate<Map<String, Object>>() {
				@Override
				public boolean apply(final Map<String, Object> input) {
					return input.get("isPrimary").equals(Boolean.TRUE);
				}
			});
			if (filtered.size() != 1) {
				continue;
			}
			final Map<String, Object> mm = filtered.iterator().next();
			final String mmName = (String) mm.get("name");

			// Determine the intervention for each Arm
			final String epochName = (String) mm.get("epochName");
			final List<Map<String, Object>> arms = (List<Map<String, Object>>) study.get("arms");
			final List<Map<String, Object>> designs = (List<Map<String, Object>>) study.get("designs");
			final List<Map<String, Object>> activities = (List<Map<String, Object>>) study.get("activities");
			final Map<String, String> armInterventions = new HashMap<>();
			for (final Map<String, Object> arm : arms) {
				if (arm.get("name") == null || ((String)arm.get("name")).isEmpty()) {
					continue;
				}
				final String armName = (String) arm.get("name");

				// Determine the activity for the arm + epoch
				final Collection<Map<String, Object>> design = Collections2.filter(designs,
						new Predicate<Map<String, Object>>() {
					@Override
					public boolean apply(final Map<String, Object> input) {
						return armName.equals(input.get("armName")) &&
								epochName.equals(input.get("epochName"));
					}
				});
				if (design.size() != 1) {
					continue;
				}
				final String activityName = (String) design.iterator().next().get("activityName");

				// Determine the invervention from the activity
				final Collection<Map<String, Object>> activity = Collections2.filter(activities,
						new Predicate<Map<String, Object>>() {
					@Override
					public boolean apply(final Map<String, Object> input) {
						return activityName.equals(input.get("name")) &&
								"TREATMENT".equals(input.get("type"));
					}
				});
				if (activity.size() != 1) {
					continue;
				}
				final List<Map<String, Object>> treatment =
						(List<Map<String, Object>>) activity.iterator().next().get("treatment");
				if(treatment.size() == 1) { // combination treatment not supported for now
					try {
						final URI drug = new URI((String) treatment.get(0).get("drugConcept"));
						if (interventionURIs.contains(drug)) {
							armInterventions.put(armName, drug.toString());
						}
					} catch (final URISyntaxException e) {
					}
				}
			}

			final List<Map<String, Object>> measurements = d_rest.getForObject(
					"{trialverse}/studies/{studyId}/measurements?variable={var}&measurementMoment={mm}",
					List.class, d_trialverse, studyId, getUUIDFromURI(variable), mmName);
			for (final Map<String, Object> m : measurements) {
				m.put("intervention", armInterventions.get(m.get("armName")));
			}
			allMeasurements.addAll(measurements);
		}

		return allMeasurements;
	}

	private static Long longValue(final Object x) {
		return ((Number)x).longValue();
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