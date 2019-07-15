package com.framework.loippi.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 敏感词验证
 * @author kviuff
 *
 */
@Documented
@Constraint(validatedBy = {WordsValidators.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface Words {
	
	String message() default "{Words.message}";
	
	Class<?>[] groups() default{};
	
	Class<? extends Payload>[] payload() default {};
	
	String field() default "";
}
