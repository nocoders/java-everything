package com.javaEverything.distributeLock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * setNx 加锁
 * lua脚本删除分布式锁
 *
 * @author linmeng
 * @date 2023/2/24 23:48
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisLockLua {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void redisLockUnlock() {
		String lockValue = UUID.randomUUID().toString().replace("-", "");
		Boolean lockRes = stringRedisTemplate.opsForValue().setIfAbsent("lockKey", lockValue, 100L, TimeUnit.SECONDS);
		if (Boolean.TRUE.equals(lockRes)) {
			System.out.println("加锁成功");
			DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript();
			defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("deleteKey.lua")));
			defaultRedisScript.setResultType(Long.class);
			Long result = stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList("lockKey"),lockValue);
			System.out.println("解锁是否成功："+ (1 == result));
		}
	}
}
