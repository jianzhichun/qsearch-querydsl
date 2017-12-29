package io.github.jianzhichun.querydsl.jpa.qsearch.sample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import io.github.jianzhichun.querydsl.qsearch.QSearchHandlerMethodArgumentResolver;
import io.github.jianzhichun.querydsl.qsearch.QSearchParam;
import io.github.jianzhichun.querydsl.qsearch.QSearchPredicator;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

@SpringBootApplication
@EnableJpaRepositories
public class QsearchQuerydslJpaSampleApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(QsearchQuerydslJpaSampleApplication.class, args);
	}

	@Override public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new QSearchHandlerMethodArgumentResolver());
	}
	
	@RestController
	@RequestMapping("/api/A")
	class AController{
		
		@Autowired ARepository repository;
		
		@PostMapping
		public A save(@RequestBody A a){
			return repository.save(a);
		}
		
		@GetMapping
		@ApiImplicitParams({
			@ApiImplicitParam(name = "q", dataType = "string", paramType = "query",
	                value = "qseach query"),
	        @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
	                value = "Results page you want to retrieve (0..N)"),
	        @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
	                value = "Number of records per page."),
	        @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
	                value = "Sorting criteria in the format: property(,asc|desc). " +
	                        "Default sort order is ascending. " +
	                        "Multiple sort criteria are supported.")
	    })
		public Page<A> qsearch(@ApiIgnore @QSearchParam(A.class) QSearchPredicator predicator, Pageable pageable){
			return repository.findAll(predicator.predicate(), pageable);
		}
	}
	
}
