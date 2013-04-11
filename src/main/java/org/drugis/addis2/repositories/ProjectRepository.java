package org.drugis.addis2.repositories;

import java.util.List;

import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	List<Project> findByOwner(User owner);
}
