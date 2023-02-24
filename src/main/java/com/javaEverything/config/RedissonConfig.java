package com.javaEverything.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 * @author linmeng
 * @date 2023/2/25 00:14
 */
@Configuration
public class RedissonConfig {
	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
				.setAddress("redis://1.117.171.139:6379").setDatabase(1);
		return Redisson.create(config);
	}
}
