package fight.it.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.common.result.Result;
import fight.it.gmall.common.result.ResultCodeEnum;
import fight.it.gmall.model.user.UserInfo;
import fight.it.gmall.user.client.UserFeignClient;

import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
@Component
    public class AuthFilter implements GlobalFilter {
   @Autowired
    UserFeignClient userFeignClient;
    //内部接口不能允许被任何外部请求访问,需要创建一个AntPathMatcher
    AntPathMatcher antPathMatcher = new AntPathMatcher();
   @Value("${authUrls.url}")
   String authUrls;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
       //获取请求与回应
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

       //获取请求地址
        String uri = request.getURI().toString();
        System.out.println(uri);
        if(uri.contains("login")||uri.contains(".css")||uri.contains(".js")||uri.contains(".jpg")||uri.contains(".png")||uri.contains(".ico")){
            return chain.filter(exchange);
        }


        //白名单

        boolean match = antPathMatcher.match("**/admin/**", uri);
        //如果请求中包含**/admin/**的路径，则返回 无权限
        if(match){
            return out(response,ResultCodeEnum.PERMISSION);
        }
        //黑名单
        //必须登录才能访问的接口 ,实例化配置文件配置 authUrls.url=trade.html,myOrder.html,pay.html
        Boolean ifWhite = false;
        String[] split = authUrls.split(",");
        for (String url : split) {
            //如果请求中
            if(uri.contains(url)){
                ifWhite = true;
            }
        }


        //远程调用sso进行身份验证验证
        String  token = getToken(request);
        Map<String, Object> userMap = null;
        //如果请求中没有token，则调用缓存中的token
        if(!StringUtils.isEmpty(token)){
            userMap = userFeignClient.verify(token);
        }
//        else{
//        //用户未登录
//         String userTempId = getUserTemplateId(request);
//            System.out.println(userTempId);
//        }

        if(null!=userMap&&userMap.get("user")!=null){
            //用户认证成功，传递用户信息
            Object user = userMap.get("user");
            UserInfo userInfo = JSON.parseObject(JSON.toJSONString(user), UserInfo.class);
            Long userId = userInfo.getId();
            //将userId放入request中
            request.mutate().header("userId",userId+"").build();
            exchange.mutate().request(request);
            return chain.filter(exchange);
        }else{
            if (ifWhite) {
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login.html?originUrl="+uri);
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }

        }
        return chain.filter(exchange);
    }

    private String getUserTemplateId(ServerHttpRequest request) {
        String  userTempId = "";
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if(null!=cookies){
            List<HttpCookie> userTemplateIdCookie = cookies.get("userTempId");
            if(null!=userTemplateIdCookie){
                for (HttpCookie httpCookie : userTemplateIdCookie) {
                    if(httpCookie.getName().equals("userTempId")){
                        userTempId = httpCookie.getValue();
                    }

                }
            }
        }
        //异步请求时 从请求头中获取userTempId
        if(StringUtils.isEmpty(userTempId)){
            HttpHeaders headers = request.getHeaders();
            if(null!=headers){
                List<String> userTemplateId1 = headers.get("userTempId");
                if(null!=userTemplateId1){
                    userTempId= userTemplateId1.get(0);
                }
            }
        }
        return userTempId;
    }

    // 接口鉴权失败返回数据
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        //返回用户没有权限登录
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
       //
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        //添加请求头信息
        response.getHeaders().add("Content-Type","applications/json;charset=UTF-8");
        //输入到页面
        Mono<Void> voidMono = response.writeWith(Mono.just(wrap));
        return voidMono;
    }

    private String getToken(ServerHttpRequest request){
        String token ="";
        //从请求中获取cookie
        MultiValueMap<String, HttpCookie> Cookies = request.getCookies();
        if(null!=Cookies){
            List<HttpCookie> tokenCookie = Cookies.get("token");
            if(null!=tokenCookie){
                for (HttpCookie httpCookie : tokenCookie) {
                     if(httpCookie.getName().equals("token")){
                           token = httpCookie.getValue();
                     }
                }
            }
        }
        // 异步请求从header中获取信息
        //如果cookie中也没有token ，则从请求头中获取
        if(StringUtils.isEmpty(token)) {
           HttpHeaders httpHeaders = request.getHeaders();
           if(null!=httpHeaders){
               List<String> token1 = httpHeaders.get("token");
               if(null!=token1){
                    token = token1.get(0);
               }
           }
        }
        return token;
    }
}
