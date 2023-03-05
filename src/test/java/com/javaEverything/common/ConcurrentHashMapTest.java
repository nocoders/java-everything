package com.javaEverything.common;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** ConcurrentHashMap学习测试类
 * @author linmeng
 * @date 2023/3/2 22:24
 */

public class ConcurrentHashMapTest {
	@Test
	public void test(){
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		for (int i = 0; i < 100; i++) {
			String key = UUID.randomUUID().toString().replace("-", "");
			String value = i + "";
			map.put(key, value);
			System.out.println();
		}
	}
}
