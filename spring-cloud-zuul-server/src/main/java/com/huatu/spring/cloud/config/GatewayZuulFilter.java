package com.huatu.spring.cloud.config;


import com.alibaba.fastjson.JSONObject;
import com.huatu.common.CommonResult;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;


/**
 * @author zhouwei
 * @Description: 网关认证过滤器
 * @create 2018-05-31 下午1:47
 **/
@Slf4j
@Component
public class GatewayZuulFilter  extends ZuulFilter {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * per：路由之前
     * routing：路由时
     * post：路由后
     * error：错误时调用
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器顺序，类似@Filter中的order
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("token");
        if(token == null) {
                //TODO  权限校验
                log.info("-------GatewayZuulFilter---token不能为空------------");
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
                JSONObject r = new JSONObject();
                r.put("code", CommonResult.PERMISSION_DENIED.getCode());
                r.put("message", CommonResult.PERMISSION_DENIED.getMessage());
                ctx.setResponseBody(r.toJSONString());
            return null;
        }else {
            Long id = Long.parseLong(Optional.ofNullable(redisTemplate.opsForHash().get(token,"id")).orElse("0").toString());
            if (id==0) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                JSONObject r = new JSONObject();
                r.put("code", CommonResult.PERMISSION_DENIED.getCode());
                r.put("message", CommonResult.PERMISSION_DENIED.getMessage());
                ctx.setResponseBody(r.toJSONString());
                return null;
            }

                        //TODO collect  user info
            log.info("---todo---记录 客户端来源同步到hbase------");
           String cv =  request.getHeader("cv");
          String terminal =  request.getHeader("terminal");
            String loginUserId = id+"";

            ctx.setRequest(new HttpServletRequestWrapper(RequestContext.getCurrentContext().getRequest()) {
                @Override
                public ServletInputStream getInputStream() throws IOException {
                    return new ServletInputStreamWrapper(loginUserId.getBytes());
                }

                @Override
                public int getContentLength() {
                    return loginUserId.getBytes().length;
                }

                @Override
                public long getContentLengthLong() {
                    return loginUserId.getBytes().length;
                }
            });

        }
        return null;
    }
}
