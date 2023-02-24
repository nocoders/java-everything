package com.javaEverything.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author linmeng
 * @date 2023/2/24 17:19
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonTest {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void test() {
		stringRedisTemplate.opsForValue().set("sdf", "sdf", 1000L, TimeUnit.MINUTES);
		System.out.println(stringRedisTemplate.opsForValue().get("sdf"));
	}
}
