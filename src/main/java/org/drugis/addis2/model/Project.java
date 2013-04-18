package org.drugis.addis2.model;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;

import lombok.Data;

@Entity
public @Data class Project {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
	@ManyToOne @JsonIgnore public User owner;
	@Column public String shortName;
	@Column public String description;
	@Column public String objective;
	@Column @CreatedDate public Date createdAt;
	@Column public Date updatedAt;
	@OneToOne(cascade = CascadeType.ALL) public Population population;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) public List<Intervention> interventions;
}

