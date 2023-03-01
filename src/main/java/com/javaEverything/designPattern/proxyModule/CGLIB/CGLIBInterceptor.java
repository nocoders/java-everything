package com.javaEverything.designPattern.proxyModule.CGLIB;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author linmeng
 * @date 2023/2/24 14:10
 */

public class CGLIBInterceptor implements MethodInterceptor {
	/**
	 * @param o           被代理对象
	 * @param method      被拦截的方法
	 * @param objects     方法入参
	 * @param methodProxy 用于调用元素方法
	 * @return java.lang.Object
	 * @Author linmeng
	 * @Description
	 * @date 2023/2/24 14:11
	 **/
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("调用方法前打印方法名称；" + method.getName());
		Object res = methodProxy.invokeSuper(o, objects);
		System.out.println("调用方法后打印方法名称；" + method.getName());
		return res;
	}
}
