package processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仿lombok，编译器生成$get/$set方法
 */
//@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface MyData {
}
