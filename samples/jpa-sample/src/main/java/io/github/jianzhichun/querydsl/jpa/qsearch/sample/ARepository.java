package io.github.jianzhichun.querydsl.jpa.qsearch.sample;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.jianzhichun.querydsl.qsearch.IQSearch;

@Repository 
public interface ARepository extends JpaRepository<A, Integer>, IQSearch<A, QA> {};