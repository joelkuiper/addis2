package org.drugis.addis2.model;

import java.util.Map;

public interface Concept {
	public String getConceptUrl();
	public Map<String, Object> getConceptProperties();
	public void setConceptProperties(Map<String, Object> props);
}