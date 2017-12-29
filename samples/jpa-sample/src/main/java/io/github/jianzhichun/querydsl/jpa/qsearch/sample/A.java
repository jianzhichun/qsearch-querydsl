package io.github.jianzhichun.querydsl.jpa.qsearch.sample;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data @NoArgsConstructor
public class A {
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id @JsonIgnore private Integer id;
	private Date date;
	private LocalDateTime localDateTime;
	private ZonedDateTime zonedDateTime;
	private boolean primitiveBoolean;
	private Boolean wrapperBoolean;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="b_id") private B b;
}
