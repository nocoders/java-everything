package com.javaEverything.proxyModule.jdk;

import java.lang.reflect.Proxy;

/**
 * @author linmeng
 * @date 2023/2/22 00:44
 */

public class JDKProxy {
	public static Object getProxy(Object target) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new JdkInvocationHandler(target));
	}

}
