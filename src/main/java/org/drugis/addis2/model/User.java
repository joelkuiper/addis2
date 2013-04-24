package org.drugis.addis2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data public class User {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
	@Column(unique=true, nullable=false) public String openid;

	public User() {
	}

	public User(final String openid) {
		this.openid = openid;
	}
}