package io.github.jianzhichun.querydsl.qsearch;

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.NonNull;

public class QSearchHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().isAssignableFrom(QSearchPredicator.class);
	}

	@Override public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		@NonNull QSearchParam qSearchParam = parameter.getParameterAnnotation(QSearchParam.class);
		return QSearchPredicator.of(Optional.ofNullable(webRequest.getParameter(qSearchParam.qname())).orElse(""), qSearchParam.value());
	}

}
