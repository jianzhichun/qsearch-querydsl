package io.github.jianzhichun.querydsl.qsearch;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.primitives.Primitives;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings({"rawtypes", "unchecked"})
@RequiredArgsConstructor(staticName="of")
public class QSearchPredicator {

	private final static Pattern PATTERN = Pattern
			.compile("\\s*(?<logicalOperator>AND|OR|,)?\\s+(?<key>[\\w|.]+?)(?<operation>~|=|<|>|<=|>=|!=)(?<value>[^=|^>|^<|^!|^~|^,|^\\s]+)");
	
	@NonNull private final String q;
	@NonNull private final Class clazz;
	@Setter private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	@Getter(lazy=true) private final PathBuilder pathBuilder = new PathBuilder<>(clazz, StringUtils.uncapitalize(ClassUtils.getShortName(clazz)));
	
	public Predicate predicate(){
		final Matcher matcher = PATTERN.matcher("AND " + q);
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<QSearchCriteria>(){

				@Override public boolean hasNext() {
					return matcher.find();
				}
	
				@Override public QSearchCriteria next() {
					return new QSearchCriteria(matcher.group("logicalOperator").equals("OR"), matcher.group("key"), matcher.group("operation"), matcher.group("value"));
				}}, Spliterator.ORDERED), false)
				.filter(QSearchCriteria::valid)
				.reduce(getPathBuilder().isNotNull(),
					(be, sc) -> sc.getOr() ? be.or(sc.getBooleanExpression()) : be.and(sc.getBooleanExpression()),
					BooleanExpression::and);
	}
	
	@AllArgsConstructor @Data
	public class QSearchCriteria{
		
		private Boolean or;
		private String key;
		private String operation;
		private String value;
		@Getter(lazy=true) private final BooleanExpression booleanExpression = booleanExpression();
		
		public boolean valid(){
			return Objects.nonNull(getBooleanExpression());
		}
		
		private <SunOfNumber extends Number & Comparable<?>, SunOfDate extends Date> BooleanExpression booleanExpression() {
			Class fieldClass = StreamSupport.stream(Splitter.on(".").split(getKey()).spliterator(), false)
				.<Class>reduce(clazz, (_clazz, field) -> {
					try {
						return _clazz.getDeclaredField(field).getType();
					} catch (NullPointerException | NoSuchFieldException | SecurityException e) {
						return null;
					}
				}, (c1, c2) -> null);
			if(Objects.isNull(fieldClass)) return null;
			fieldClass = fieldClass.isPrimitive() ? Primitives.wrap(fieldClass) : fieldClass;
			if(ClassUtils.isAssignable(Number.class, fieldClass))
				return fromNumber((Class<SunOfNumber>)fieldClass);
			else if(ClassUtils.isAssignable(Date.class, fieldClass))
				return fromDate((Class<SunOfDate>)fieldClass);
			else if(ClassUtils.isAssignable(ZonedDateTime.class, fieldClass))
				return fromZonedDateTime((Class<ZonedDateTime>)fieldClass);
			else if(ClassUtils.isAssignable(LocalDateTime.class, fieldClass))
				return fromLocalDateTime((Class<LocalDateTime>)fieldClass);
			else if(ClassUtils.isAssignable(Boolean.class, fieldClass))
				return fromBoolean((Class<Boolean>)fieldClass);
			else 
				return fromString(fieldClass);
		}
		
		private BooleanExpression fromString(Class _clazz) {
			StringPath path = getPathBuilder().getString(getKey());
			switch (getOperation()) {
			case "~": return path.containsIgnoreCase(getValue());
			case "=": return path.eq(getValue());
			case "!=": return path.ne(getValue());
			default: return null;
			}
		}

		private BooleanExpression fromBoolean(Class _clazz) {
			BooleanPath path = getPathBuilder().getBoolean(getKey());
			Boolean value = Boolean.parseBoolean(getValue());
			switch (getOperation()) {
			case "=": return path.eq(value);
			case "!=": return path.ne(value);
			default: return null;
			}
		}

		private BooleanExpression fromLocalDateTime(Class<LocalDateTime> _clazz) {
			DateTimePath<LocalDateTime> path = getPathBuilder().getDateTime(getKey(), _clazz);
			LocalDateTime value = LocalDateTime.parse(getValue(), dateTimeFormatter);
			switch (getOperation()) {
			case "=": return path.eq(value); 
			case ">": return path.gt(value); 
			case "<": return path.lt(value); 
			case ">=": return path.goe(value); 
			case "<=": return path.loe(value); 
			case "!=": return path.ne(value); 
			default: return null;
			}
		}
		
		private BooleanExpression fromZonedDateTime(Class<ZonedDateTime> _clazz) {
			DateTimePath<ZonedDateTime> path = getPathBuilder().getDateTime(getKey(), _clazz);
			ZonedDateTime value = ZonedDateTime.parse(getValue());
			switch (getOperation()) {
			case "=": return path.eq(value); 
			case ">": return path.gt(value); 
			case "<": return path.lt(value); 
			case ">=": return path.goe(value); 
			case "<=": return path.loe(value); 
			case "!=": return path.ne(value); 
			default: return null;
			}
		}

		private <SunOfDate extends Date> BooleanExpression fromDate(Class<SunOfDate> _clazz) {
			DatePath<Date> path = getPathBuilder().getDate(getKey(), _clazz);
			Date value = Date.from(ZonedDateTime.parse(getValue(), dateTimeFormatter).toInstant());
			switch (getOperation()) {
			case "=": return path.eq(value); 
			case ">": return path.gt(value); 
			case "<": return path.lt(value); 
			case ">=": return path.goe(value); 
			case "<=": return path.loe(value); 
			case "!=": return path.ne(value); 
			default: return null;
			}
		}

		private <SunOfNumber extends Number & Comparable<?>> BooleanExpression fromNumber(Class<SunOfNumber> _clazz) {
			NumberPath<SunOfNumber> path = getPathBuilder().getNumber(getKey(), _clazz);
			SunOfNumber value = NumberUtils.parseNumber(getValue(), _clazz);
			switch (getOperation()) {
			case "=": return path.eq(value); 
			case ">": return path.gt(value); 
			case "<": return path.lt(value); 
			case ">=": return path.goe(value); 
			case "<=": return path.loe(value); 
			case "!=": return path.ne(value); 
			default: return null;
			}
		}
	}
	
}
