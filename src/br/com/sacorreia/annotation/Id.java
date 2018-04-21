package br.com.sacorreia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
	static enum ID {
		AUTO_INCREMENT, NORMAL
	}

	ID value() default ID.NORMAL;
}
