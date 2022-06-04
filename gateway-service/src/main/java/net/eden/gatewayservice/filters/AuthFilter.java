package net.eden.gatewayservice.filters;

import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import net.eden.gatewayservice.filters.AuthGatewayFilterFactory.Config;
import net.eden.gatewayservice.util.GatewayAssistant;
import net.eden.gatewayservice.util.JwtAssistant;
import reactor.core.publisher.Mono;
/**
 * Redis有两种键值对
 * 1.String u_id:Set<Long pid>
 * 2.String Priv:Map<String path+method,Long pid>
 */
public class AuthFilter implements GatewayFilter, Ordered{

    private static final String TOKEN = "token";

    public AuthFilter(Config config) {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 验证和授权,权限过滤器
     * 0. 看用户想干啥
     * 1. 校验 token 是否需要换发token
     * 2. 从redis中读取用户权限信息，如果该用户的权限信息不在redis中，调用微服务load用户权限信息
     * 3. 判断用户是否有权限访问该url
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        
        /* *************** 一、检查 hk = path + method in Priv ? *************** */
        //1.get path
        String path = request.getPath().toString();
        //2.get method
        String method = request.getMethod().toString();
        //3.path format,数字 -> /{id} == url
        Pattern pattern = Pattern.compile("/[1-9][0-9]*");
        String url = pattern.matcher(path).replaceAll("/{id}");
        //4.get hashkey = url + "-" + method
        String hk = url + "-" + method;
        //5.get pid 
        String pid = GatewayAssistant.getpid(hk);   //long pid
        System.out.println(String.format("hk = %s\npid = %s", hk,pid));

        //test
        // if(redisTemplate==null && userServiceClient==null){
        //     response.setRawStatusCode(502);
        //     return response.writeWith(Mono.empty());
        // }
        if(pid == null){
            //检查hk是否存在，可以放到全局过滤器中
            response.setRawStatusCode(404);
            return response.writeWith(Mono.empty());
        }

        /* *************** 2. 校验token *************** */
        //1. 从请求头获取token
        String token = request.getHeaders().getFirst(TOKEN);
        //2. 校验token是否合法
        JwtAssistant.TokenInfo tokenInfo = JwtAssistant.verifyToken(token);
        if(tokenInfo == null){
            //token 不合法
            response.setRawStatusCode(401);
            return response.writeWith(Mono.empty());
        }
        //3. 获得uid和过期时间
        Long uid = tokenInfo.getUid();
        Date expireAt = tokenInfo.getExpireTime();
        //4. 检查是否需要换发token,16、15
        Long timeleft = expireAt.getTime() - System.currentTimeMillis();
        if(timeleft < 15*24*60*60*1000l){
            token = JwtAssistant.initToken(uid, 16);
        }
        response.getHeaders().set(TOKEN, token);
       
        /* *************** 3. 用户权限信息（uid:pids） in redis? *************** */
        //检查redis中是否有用户的权限信息，没有则通过RPC调用用户服务加载数据
        GatewayAssistant.loadPidsIfAbsent(uid); //可以改成先get再异步load
        
        /* *************** 4. pid in pids? *************** */
        if(!GatewayAssistant.checkPermissons(uid, pid)){
            response.setRawStatusCode(403);
            return response.writeWith(Mono.empty());
        }else{
            return chain.filter(exchange);
        }
    }
}
