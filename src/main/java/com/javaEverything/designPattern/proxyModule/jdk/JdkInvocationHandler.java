package com.javaEverything.designPattern.proxyModule.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author linmeng
 * @date 2023/2/22 00:42
 */
public class JdkInvocationHandler implements InvocationHandler {
	/**
	 * 被代理类
	 **/
	private Object target;

	public JdkInvocationHandler(Object target) {
		this.target = target;
	}

	/**
	 * @Author linmeng
	 * @Description
	 * @date 2023/2/24 15:57
	 * @param proxy 动态生成的代理类
	 * @param method 代理方法
	 * @param args 方法参数
	 * @return java.lang.Object
	 **/
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("调用方法前打印方法名称；" + method.getName());
		Object res = method.invoke(target, args);
		System.out.println("调用方法后打印方法名称；" + method.getName());
		return res;
	}
}
