package org.drugis.addis2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class Population {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
	@Column public String indicationConceptUrl; 
}
