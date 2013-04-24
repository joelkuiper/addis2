package org.drugis.addis2.view.helper;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;

public class FormProcessor extends AbstractAttrProcessor {
	static final String ATTRIBUTE_NAME_FORM = "intercept";

	public FormProcessor() {
		super(ATTRIBUTE_NAME_FORM);
	}

	@Override
	protected ProcessorResult processAttribute(
			final Arguments arguments,
			final Element element,
			final String attributeName) {
		if (element.getNormalizedName() != "form") {
			throw new TemplateProcessingException("Form annontation must be used on a form element");
		}

		element.removeAttribute(attributeName);
		addExtraHiddenFields(arguments, element);

		return ProcessorResult.OK;
	}

	private void addExtraHiddenFields(
			final Arguments arguments,
			final Element element) {
		final RequestContext requestContext = (RequestContext) arguments.getContext().getVariables()
				.get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
		final HttpServletRequest request = ((IWebContext) (arguments.getContext())).getHttpServletRequest();

		final Map<String, String> extraHiddenFields = requestContext.getRequestDataValueProcessor().getExtraHiddenFields(
				request);

		for (final Map.Entry<String, String> hidden : extraHiddenFields.entrySet()) {
			addHidden(element, hidden.getKey(), hidden.getValue());
		}
	}

	private void addHidden(
			final Element form,
			final String name,
			final String value) {
		final Element hiddenElement = new Element("input");
		hiddenElement.setAttribute("type", "hidden");
		hiddenElement.setAttribute("name", name);
		hiddenElement.setAttribute("value", value);

		form.addChild(hiddenElement);
	}

	@Override
	public int getPrecedence() {
		return 1300;
	}

}
