package com.crivano.jsync;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreForSimilarityIfCondition {

	String[] condition() default { "", "" };

}
