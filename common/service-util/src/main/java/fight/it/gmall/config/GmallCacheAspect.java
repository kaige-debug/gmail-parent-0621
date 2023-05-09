package fight.it.gmall.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {
    @Autowired
    RedisTemplate redisTemplate;
    @Around("@annotation(fight.it.gmall.config.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point)  {
        Object result =null;
        String  cacheKey = "";
        //获取方法信息，methodSignature这里可以理解为单例方法
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        String name = methodSignature.getMethod().getName();
        cacheKey = name;
        // 返回类型和参数
        Class returnType = methodSignature.getMethod().getReturnType();
        Object[] args = point.getArgs();
        for (Object arg : args) {
            cacheKey = cacheKey +":"+arg;
        }
        //获取方法注解信息
        GmallCache annotation = methodSignature.getMethod().getAnnotation(GmallCache.class);
        //访问缓存db，结果付给result
        result = redisTemplate.opsForValue().get(cacheKey);
        if(null == result){
            try{
                //获取分布式锁
                String key = UUID.randomUUID().toString();
                Boolean ok = redisTemplate.opsForValue().setIfAbsent(cacheKey+":lock",key,2, TimeUnit.SECONDS);
                //如果获取到锁
                if(ok){
                    // 执行被代理方法
                    result= point.proceed();
                    if(null==result){
                        //如果被代理方法的返回结果为空，把空存入缓存
                        redisTemplate.opsForValue().set(cacheKey, result,5,TimeUnit.SECONDS);
                    }else{
                        // 如果被代理的方法的返回结果不为空，同步缓存
                        redisTemplate.opsForValue().set(cacheKey, result);

                    }
                    //删除锁
                    String openKey =(String) redisTemplate.opsForValue().get(cacheKey + ":lock");
                    if(key.equals(openKey)){
                        redisTemplate.delete(cacheKey +":lock");
                    }

                }else{//没有获取到锁
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    return redisTemplate.opsForValue().get(cacheKey);//过n秒之后重新查询缓存
                }
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }

        }
        return result;
    }
}
