package org.drugis.addis2.service;

import java.util.Map;

import org.drugis.addis2.model.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TrialverseService {
	@Autowired private RestTemplate d_rest;

	@SuppressWarnings("unchecked")
	public boolean fetchConceptProperties(Concept concept) {
		try {
			concept.setConceptProperties(d_rest.getForObject(concept.getConceptUrl(), Map.class));
		} catch (Exception e) {
			// FIXME: log
			return false;
		}
		return true;
	}
}
