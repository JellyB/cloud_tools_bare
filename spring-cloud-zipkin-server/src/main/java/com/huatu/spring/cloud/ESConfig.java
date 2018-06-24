package com.huatu.spring.cloud;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author hanchao
 * @date 2018/1/23 16:11
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.huatu.search")
public class ESConfig {
}
