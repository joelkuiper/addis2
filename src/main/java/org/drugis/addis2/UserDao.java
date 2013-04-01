package org.drugis.addis2;

import java.util.Collection;

import org.springframework.dao.DataAccessException;

public interface UserDao {
    /**
     * Find all persons.
     */
    public Collection<User> findUsers() throws DataAccessException;
    
    /**
     * Find persons by last name.
     */
    public User findUserByEmail(String email) throws DataAccessException;
    
    /**
     * Saves person.
     */
    public void save(User user);
}