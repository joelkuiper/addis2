package org.drugis.addis2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User {
	@Id public long id;
	@Column public String openid;
	
	@Override
	public String toString() {
		return openid;
	}
}