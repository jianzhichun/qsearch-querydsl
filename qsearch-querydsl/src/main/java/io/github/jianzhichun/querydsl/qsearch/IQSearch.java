package io.github.jianzhichun.querydsl.qsearch;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import com.querydsl.core.types.dsl.EntityPathBase;

public interface IQSearch<T, QT extends EntityPathBase<T>> extends QueryDslPredicateExecutor<T>, QuerydslBinderCustomizer<QT> {

	@Override default void customize(QuerydslBindings bindings, QT root) {}
}
