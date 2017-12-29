##  QSEARCH-QUERYDSL

querydsl extension for searching pojo via custom query like lucene query. 

e.g. b.name=hello AND date<2017-12-29T08:29:55.907Z

基于querydesl和spring-data的自定义搜索， 快速搭建搜索语法

[![Build Status](https://travis-ci.org/jianzhichun/qsearch-querydsl.svg?branch=master)](https://travis-ci.org/jianzhichun/qsearch-querydsl)
[![Coverage Status](https://coveralls.io/repos/github/jianzhichun/qsearch-querydsl/badge.svg?branch=master)](https://coveralls.io/github/jianzhichun/qsearch-querydsl?branch=master)


**Quickstart**

Add below dependencies to your pom
```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>

<dependency>
    <groupId>io.github.jianzhichun</groupId>
    <artifactId>qsearch-querydsl</artifactId>
    <version>0.0.1</version>
</dependency>
```
And plugin for generating source
```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>maven-apt-plugin</artifactId>
    <version>1.0.4</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Add interface [IQSearch<T, QT extends EntityPathBase<T>>](/qsearch-querydsl/src/main/java/io/github/jianzhichun/querydsl/qsearch/IQSearch.java) to your repository assigned to [CrudRepository](https://github.com/spring-projects/spring-data-commons/blob/master/src/main/java/org/springframework/data/repository/CrudRepository.java)
```Java
@Repository 
public interface ARepository extends JpaRepository<A, Integer>, IQSearch<A, QA> {};
```
Add [QSearchHandlerMethodArgumentResolver](/qsearch-querydsl/src/main/java/io/github/jianzhichun/querydsl/qsearch/QSearchHandlerMethodArgumentResolver.java) to your List<[HandlerMethodArgumentResolver](https://github.com/spring-projects/spring-framework/blob/master/spring-web/src/main/java/org/springframework/web/method/support/HandlerMethodArgumentResolver.java)> in spring context
```Java
public class QsearchQuerydslJpaSampleApplication extends WebMvcConfigurerAdapter {

    @Override public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new QSearchHandlerMethodArgumentResolver());
}
```
Add restful function with parameter [QSearchPredicator](/qsearch-querydsl/src/main/java/io/github/jianzhichun/querydsl/qsearch/QSearchPredicator.java) with annotation [@QSearchParam](/qsearch-querydsl/src/main/java/io/github/jianzhichun/querydsl/qsearch/QSearchParam.java)(T.class)
```Java
@GetMapping

public Page<A> qsearch(@ApiIgnore @QSearchParam(A.class) QSearchPredicator predicator, Pageable pageable){
    return repository.findAll(predicator.predicate(), pageable);
}
```
Then you can query pojo via qsearch pattern

 Pattern: 
```Java
private final static Pattern PATTERN = Pattern
        .compile("\\s*(?<logicalOperator>AND|OR|,)?\\s+(?<key>[\\w|.]+?)(?<operation>~|=|<|>|<=|>=|!=)(?<value>[^=|^>|^<|^!|^~|^,|^\\s]+)");
```

* *logicalOperator*: OR means or query, AND | , means and query
* *key*: Name of pojo's filed, supported dot path 
* *operation*: 
*   * ~: containsIgnoreCase in String query
    * =: equals
    * < | > | <= | >= | !=: compare

e.g. [http://localhost:8080/api/A?q=b.name~tri, date>=2017-12-29T08:29:55.907Z]()

![qsearch-jpa-q](/samples/jpa-sample/images/qsearch-jpa-q.png)

**Examples**

[qsearch-querydsl example projects](/samples)

**License**

This project is licensed under Apache License 2.0.