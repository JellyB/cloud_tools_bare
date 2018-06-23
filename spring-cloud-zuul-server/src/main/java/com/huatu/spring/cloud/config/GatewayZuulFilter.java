package com.huatu.spring.cloud.config;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * @author zhouwei
 * @Description: 网关认证过滤器
 * @create 2018-05-31 下午1:47
 **/
@Slf4j
@Component
public class GatewayZuulFilter  extends ZuulFilter {
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
            log.info("----------success------------");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(404);
            ctx.setResponseBody("token cannot be empty");
            return null;
        }else {
            //TODO collect  user info
            log.info("---todo---记录 客户端来源同步到hbase------");
          // String cv =  request.getHeader("cv");
        //   String terminal =  request.getHeader("terminal");
        }
        return null;
    }
}
