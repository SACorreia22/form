package br.com.sacorreia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	static enum TYPE {
		BIT, CHAR, DATE, DATETIME, NUMBER, TIMESTAMP, VARCHAR
	}

	String name();

	TYPE type();

	int size() default -1;

	String valueDefault() default "";

	boolean required() default false;
}
