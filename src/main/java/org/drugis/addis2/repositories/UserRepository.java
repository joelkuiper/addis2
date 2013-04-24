package org.drugis.addis2.repositories;

import org.drugis.addis2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByOpenid(final String openid);
}
