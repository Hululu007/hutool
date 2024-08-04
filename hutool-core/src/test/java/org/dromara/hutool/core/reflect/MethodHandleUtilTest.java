/*
 * Copyright (c) 2024 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.core.reflect;

import org.dromara.hutool.core.classloader.ClassLoaderUtil;
import org.dromara.hutool.core.reflect.method.MethodHandleUtil;
import org.dromara.hutool.core.reflect.method.MethodUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MethodHandleUtilTest {

	@Test
	public void invokeDefaultTest(){
		final Duck duck = (Duck) Proxy.newProxyInstance(
				ClassLoaderUtil.getClassLoader(),
				new Class[] { Duck.class },
				MethodHandleUtil::invoke);

		Assertions.assertEquals("Quack", duck.quack());

		// 测试子类执行default方法
		final Method quackMethod = MethodUtil.getMethod(Duck.class, "quack");
		String quack = MethodHandleUtil.invoke(new BigDuck(), quackMethod);
		Assertions.assertEquals("Quack", quack);

		// 测试反射执行默认方法
		quack = MethodUtil.invoke(new Duck() {}, quackMethod);
		Assertions.assertEquals("Quack", quack);
	}

	@Test
	public void invokeDefaultByReflectTest(){
		final Duck duck = (Duck) Proxy.newProxyInstance(
				ClassLoaderUtil.getClassLoader(),
				new Class[] { Duck.class },
				MethodUtil::invoke);

		Assertions.assertEquals("Quack", duck.quack());
	}

	@Test
	public void invokeStaticByProxyTest(){
		final Duck duck = (Duck) Proxy.newProxyInstance(
				ClassLoaderUtil.getClassLoader(),
				new Class[] { Duck.class },
				MethodUtil::invoke);

		Assertions.assertEquals("Quack", duck.quack());
	}

	@Test
	public void invokeTest(){
		// 测试执行普通方法
		final int size = MethodHandleUtil.invoke(new BigDuck(),
				MethodUtil.getMethod(BigDuck.class, "getSize"));
		Assertions.assertEquals(36, size);
	}

	@Test
	public void invokeStaticTest(){
		// 测试执行普通方法
		final String result = MethodHandleUtil.invoke(null,
				MethodUtil.getMethod(Duck.class, "getDuck", int.class), 78);
		Assertions.assertEquals("Duck 78", result);
	}

	@Test
	public void testInvokeExact() {
		final Duck duck = new BigDuck();

		final Method method = MethodUtil.getMethod(BigDuck.class, "toString");
		final Object[] args = {};

		// 执行测试方法
		final String result = MethodHandleUtil.invokeExact(duck, method, args);

		// 验证结果
		Assertions.assertEquals(duck.toString(), result);
	}

	interface Duck {
		default String quack() {
			return "Quack";
		}

		static String getDuck(final int count){
			return "Duck " + count;
		}
	}

	static class BigDuck implements Duck{
		public int getSize(){
			return 36;
		}

		@SuppressWarnings("unused")
		private String getPrivateValue(){
			return "private value";
		}

		@SuppressWarnings("unused")
		private static String getPrivateStaticValue(){
			return "private static value";
		}

		@Override
		public String toString() {
			return super.toString();
		}
	}
}
