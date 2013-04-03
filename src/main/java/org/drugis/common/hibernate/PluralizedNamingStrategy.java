package org.drugis.common.hibernate;

import java.util.Locale;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.jvnet.inflector.Noun;
import org.springframework.stereotype.Service;

@Service("pluralizedNamingStrategy")
public class PluralizedNamingStrategy extends ImprovedNamingStrategy {
	private static final long serialVersionUID = -6835213456671477403L;

	@Override
	public String classToTableName(String className) {
		String tableName = super.classToTableName(className);
		int idx = tableName.lastIndexOf('_');
		return tableName.substring(0, idx + 1) + Noun.pluralOf(tableName.substring(idx + 1), Locale.ENGLISH);
	}
}
