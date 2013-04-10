package org.drugis.addis2.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public class GenericDao <T, PK extends Serializable> {

	@PersistenceContext private EntityManager d_em;
    private final Class<T> type;
    
	public GenericDao(final Class<T> type) {
		this.type = type;
	}
		
	public EntityManager getEntityManager() { 
		return d_em;
	}
	
	@Transactional
	public T save(final T obj) {
		return d_em.merge(obj);
	}

	@Transactional(readOnly=true)
	public T get(PK id) {
		return d_em.find(this.type, id);
	}
}
