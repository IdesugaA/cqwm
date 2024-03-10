package com.own.interceptor;

import com.own.constant.JwtClaimsConstant;
import com.own.context.BaseContext;
import com.own.properties.JwtProperties;
import com.own.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response , Object handler) throws Exception{
        //判断当前拦截到的是Controller的方法还是其他资源
        if(!(handler instanceof HandlerMethod)){
            return true; //当前拦截到的不是动态方法，直接放行
        }
        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        try{
            log.info("jwt检验：{}",token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(),token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前员工id：{}",userId);
            BaseContext.setCurrentId(userId);
            //3、通过，放行
            return true;
        } catch(Exception e){
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        if(BaseContext.getCurrentId()!=null){
            BaseContext.removeCurrentId();
        }
    }
}
