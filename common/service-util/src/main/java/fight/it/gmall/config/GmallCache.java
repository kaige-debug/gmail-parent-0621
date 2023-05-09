package fight.it.gmall.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    public String skuPrefix() default "sku:";

    public String spuPrefix() default "spu:";

    public String prefix() default "GmallCache:";
}
