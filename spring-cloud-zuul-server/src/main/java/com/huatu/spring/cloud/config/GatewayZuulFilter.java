package com.huatu.spring.cloud.config;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.huatu.common.CommonResult;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;

import lombok.extern.slf4j.Slf4j;


/**
 * @author zhouwei
 * @Description: 网关认证过滤器
 * @create 2018-05-31 下午1:47
 **/
@Slf4j
@Component
public class GatewayZuulFilter extends ZuulFilter {

    private static List<String> whiteUrls;

    static {
        whiteUrls = Lists.newArrayList();
        // 获取验证码 登陆
        whiteUrls.add("/user/v1/user/phoneCode");
        whiteUrls.add("/user/v1/user/phoneLogin");
        whiteUrls.add("/user/v1/user/login");
        whiteUrls.add("/user/v1/user/thirdLogin");
        whiteUrls.add("/user/v1/user/info");
        // 题库相关
        whiteUrls.add("/tk/v1/question/record");
        whiteUrls.add("/tk/v1/video/list");
        whiteUrls.add("/tk/v1/video/answer");
        whiteUrls.add("/tk/v1/question/detail/");
        whiteUrls.add("/tk/v1/advert/");
        whiteUrls.add("/tk/v1/question/newest/");
        whiteUrls.add("/tk/v1/nationArea/");
        whiteUrls.add("/tk/v1/question/nationArea/");
        whiteUrls.add("/tk/v1/question/detail/");
        whiteUrls.add("/tk/v1/question/type/");
        whiteUrls.add("/tk/v1/nationArea/");
        whiteUrls.add("/tk/v1/question/type/root");
        whiteUrls.add("/tk/v1/question/type/");
        whiteUrls.add("/tk/v1/question/type/noLeader");
        whiteUrls.add("/tk/v1/exam/list");
        whiteUrls.add("/tk/v1/position/");
        whiteUrls.add("/tk/v1/dic/PositionInfoTag");
        whiteUrls.add("/tk/v1/organization/");
        whiteUrls.add("/tk/v1/organization/type/");
        whiteUrls.add("/tk/v1/position/option/label/");
        // 搜索相关
        whiteUrls.add("/s/v1/user/search");
        whiteUrls.add("/s/v1/question/search");
        whiteUrls.add("/s/v1/hotWord/type");
    }

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

        String url = request.getRequestURI();
        log.info("url:" + url);
        for(String whiteUrl : whiteUrls){
            if(url.startsWith(whiteUrl)){
                return null;
            }
        }

        String token = request.getHeader("token");
        if (token == null) {
            //TODO  权限校验
            log.info("-------GatewayZuulFilter---token不能为空------------");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
            JSONObject r = new JSONObject();
            r.put("code", CommonResult.PERMISSION_DENIED.getCode());
            r.put("message", CommonResult.PERMISSION_DENIED.getMessage());
            ctx.setResponseBody(r.toJSONString());
            return null;
        } else {
            log.info("-------GatewayZuulFilter---token不能为空------------token值:" + token);
            Long id = Long.parseLong(Optional.ofNullable(redisTemplate.opsForHash().get(token, "id")).orElse("0").toString());
            log.info("-------GatewayZuulFilter---token不能为空------------id值:" + id);
            if (id == 0) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                JSONObject r = new JSONObject();
                r.put("code", CommonResult.PERMISSION_DENIED.getCode());
                r.put("message", CommonResult.PERMISSION_DENIED.getMessage());
                ctx.setResponseBody(r.toJSONString());
                return null;
            }
            String cv = request.getHeader("cv");
            String terminal = request.getHeader("terminal");
            log.info("{}$$${}$$${}", request.getRequestURI(), cv, terminal);
//            try {
//                InputStream in = request.getInputStream();
//            String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
//            JSONObject json = JSONObject.parseObject(body);
//            if(json!=null){
//                json.put("loginUserId", id);
//            }else{
//                json = new JSONObject();
//                json.put("loginUserId", id);
//            }
//
//            String newBody = json.toString();
//
//            ctx.setRequest(new HttpServletRequestWrapper(RequestContext.getCurrentContext().getRequest()) {
//                @Override
//                public ServletInputStream getInputStream() throws IOException {
//                    return new ServletInputStreamWrapper(newBody.getBytes());
//                }
//
//                @Override
//                public int getContentLength() {
//                    return newBody.getBytes().length;
//                }
//
//                @Override
//                public long getContentLengthLong() {
//                    return newBody.getBytes().length;
//                }
//            });
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            // 添加用户ID参数
            ctx.getRequest().getParameterMap();
            Map<String, List<String>> requestParams = ctx.getRequestQueryParams();
            if (requestParams == null) {
            	requestParams = Maps.newHashMap();
            	ctx.setRequestQueryParams(requestParams);
            }
            requestParams.put("loginUserId", Arrays.asList(id + ""));

        }
        return null;
    }
}
