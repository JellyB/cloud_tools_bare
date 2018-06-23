package com.huatu.spring.cloud.config;
import com.alibaba.fastjson.JSONObject;
import com.huatu.common.CommonResult;
import com.huatu.common.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParser;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhouwei
 * @Description: 路由发起请求失败时的回滚处理
 * @create 2018-05-31 下午1:50
 **/
@Slf4j
@Component
public class GatewayZuulFallback implements ZuulFallbackProvider {
    /**
     * 需要所有调用都支持回退，则return "*"或return null
     * @return
     */
    @Override
    public String getRoute() {
        return "*";
    }
    /**
     * 如果请求用户服务失败，返回什么信息给消费者客户端
     */
    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                log.error("-----------getStatusCode------------");
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                log.error("-----------getRawStatusCode------------");
                return this.getStatusCode().value();
            }

            @Override
            public String getStatusText() throws IOException {
                log.error("-----------getStatusText------------");
                return this.getStatusCode().getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                log.error("-----------come from zuul fallback------------");
                JSONObject r = new JSONObject();
                r.put("code", CommonResult.SERVICE_INTERNAL_ERROR.getCode());
                r.put("message", "系统繁忙，请稍后重试...");
                return new ByteArrayInputStream(r.toJSONString().getBytes("UTF-8"));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return httpHeaders;
            }
        };
    }
}
