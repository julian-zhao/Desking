package org.desking.model.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于实体类的实例变量。指明该实例变量对应的数据库字段名称。
 * <dl>
 * 		<dt>1. 当实例变量未声明"@Entity"时，表示该变量值直接对应数据库字段值。</dt>
 * 		<dt>2. 当实例变量被声明"@Entity"时，表示该变量值为另一关系实体，并且该实体的ID值等于所设置的value值。</dt>
 * </dl>
 * 
 * @see #Entity
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface COLUMN {
	String value();
}
