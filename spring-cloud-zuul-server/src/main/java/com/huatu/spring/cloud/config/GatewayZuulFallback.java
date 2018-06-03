package com.huatu.spring.cloud.config;
import lombok.extern.slf4j.Slf4j;
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
                return this.getStatusCode().value();// 500
            }

            @Override
            public String getStatusText() throws IOException {
                log.error("-----------getStatusText------------");
                return this.getStatusCode().getReasonPhrase();//  Internal Server Error
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                log.error("-----------getBody------------");
                return new ByteArrayInputStream(getStatusText().getBytes());
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
