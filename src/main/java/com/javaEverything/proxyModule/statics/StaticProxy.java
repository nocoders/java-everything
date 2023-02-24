package com.javaEverything.proxyModule.statics;

import com.javaEverything.proxyModule.Image;
import com.javaEverything.proxyModule.RealImage;

/**
 * @author linmeng
 * @date 2023/2/22 00:26
 */

public class StaticProxy implements Image {
	private RealImage realImage;
	@Override
	public void display(String name) {
		realImage = new RealImage();
		System.out.println("start");
		realImage.display(name);
		System.out.println("end");
	}

	public static void main(String[] args) {
		Image image = new StaticProxy();
		image.display("图片");
	}
}
