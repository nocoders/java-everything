package com.javaEverything.proxyModule.CGLIB;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author linmeng
 * @date 2023/2/24 14:15
 */
public class CGLIBProxy {
	public static Object getProxy(Class<?> clazz) {
		// 动态代理增强类
		Enhancer enhancer = new Enhancer();
		// 类加载器
		enhancer.setClassLoader(clazz.getClassLoader());
		// 被代理类
		enhancer.setSuperclass(clazz);
		// 增强类
		enhancer.setCallback(new CGLIBInterceptor());
		// 代理类创建
		return enhancer.create();
	}
}
