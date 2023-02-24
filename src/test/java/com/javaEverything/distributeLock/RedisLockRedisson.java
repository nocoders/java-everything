package com.javaEverything.distributeLock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
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
import java.util.concurrent.locks.Lock;

/**
 * setNx 加锁
 * lua脚本删除分布式锁
 *
 * @author linmeng
 * @date 2023/2/24 23:48
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisLockRedisson {

	@Autowired
	private RedissonClient redissonClient;

	@Test
	public void redisLockUnlock() {
		Lock lock = redissonClient.getLock("lock");
		try {
			lock.lock();
			System.out.println("加锁成功");
			TimeUnit.SECONDS.sleep(5);
		}catch (Exception e){}
		finally {
			lock.unlock();
			System.out.println("解锁成功：");

		}
	}
}
