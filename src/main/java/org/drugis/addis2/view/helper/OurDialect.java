package org.drugis.addis2.view.helper;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class OurDialect extends AbstractDialect {

	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public boolean isLenient() {
		return false;
	}

	@Override
    public Set<IProcessor> getProcessors() {
		final Set<IProcessor> tagProcessors = new HashSet<>();
		tagProcessors.add(new FormProcessor());
		return tagProcessors;
	}
}
