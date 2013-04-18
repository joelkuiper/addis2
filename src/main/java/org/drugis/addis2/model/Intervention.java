package org.drugis.addis2.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import lombok.Data;

@Entity
public @Data class Intervention implements Concept {
	@Id @JsonIgnore @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
	@Column public String conceptUrl; 
	
	@Transient public Map<String, Object> conceptProperties;
}
