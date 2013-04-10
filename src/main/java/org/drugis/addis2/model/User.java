package org.drugis.addis2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class User {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
	@Column(unique=true, nullable=false) public String openid;
	
	public User() {
	}
	
	public User(String openid) {
		this.openid = openid;
	}
}