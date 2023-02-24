package com.javaEverything.proxyModule.CGLIB;

import com.javaEverything.proxyModule.RealImage2;
import org.junit.Test;

/**
 * @author linmeng
 * @date 2023/2/22 00:50
 */

public class CGLIBTest {
	@Test
	public void cglibTest(){
		RealImage2 proxy = (RealImage2) CGLIBProxy.getProxy(RealImage2.class);
		proxy.display("图片");
	}
}
