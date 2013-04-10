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
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		if (element.getNormalizedName() != "form") {
			throw new TemplateProcessingException("Form annontation must be used on a form element");
		}

		element.removeAttribute(attributeName);
		addExtraHiddenFields(arguments, element);

		return ProcessorResult.OK;
	}

	private void addExtraHiddenFields(Arguments arguments, Element element) {
		final RequestContext requestContext = (RequestContext) arguments.getContext().getVariables()
				.get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
		HttpServletRequest request = ((IWebContext) (arguments.getContext())).getHttpServletRequest();

		Map<String, String> extraHiddenFields = requestContext.getRequestDataValueProcessor().getExtraHiddenFields(
				request);

		for (Map.Entry<String, String> hidden : extraHiddenFields.entrySet()) {
			addHidden(element, hidden.getKey(), hidden.getValue());
		}
	}

	private void addHidden(Element form, String name, String value) {
		Element hiddenElement = new Element("input");
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
