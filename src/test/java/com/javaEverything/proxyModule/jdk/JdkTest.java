package com.javaEverything.proxyModule.jdk;

import com.javaEverything.designPattern.proxyModule.Image;
import com.javaEverything.designPattern.proxyModule.RealImage;
import com.javaEverything.designPattern.proxyModule.jdk.JDKProxy;
import org.junit.Test;

/**
 * @author linmeng
 * @date 2023/2/22 00:50
 */

public class JdkTest {
	@Test
	public void jdkTest(){
		Image proxy = (Image) JDKProxy.getProxy(new RealImage());
		proxy.display("图片");
	}
}
