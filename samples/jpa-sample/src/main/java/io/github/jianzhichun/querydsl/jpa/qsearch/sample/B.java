package io.github.jianzhichun.querydsl.jpa.qsearch.sample;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data @NoArgsConstructor
public class B {
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id @JsonIgnore private Integer id;
	private String name;
}
