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

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 来自：org.apache.dubbo.common.utils.ClassDescUtilTest
 */
public class ClassDescTest {

	@Test
	void descToClassTest() {
		assertEquals(void.class, ClassDescUtil.descToClass("V"));
		assertEquals(boolean.class, ClassDescUtil.descToClass("Z"));
		assertEquals(boolean[].class, ClassDescUtil.descToClass("[Z"));
		assertEquals(byte.class, ClassDescUtil.descToClass("B"));
		assertEquals(char.class, ClassDescUtil.descToClass("C"));
		assertEquals(double.class, ClassDescUtil.descToClass("D"));
		assertEquals(float.class, ClassDescUtil.descToClass("F"));
		assertEquals(int.class, ClassDescUtil.descToClass("I"));
		assertEquals(long.class, ClassDescUtil.descToClass("J"));
		assertEquals(short.class, ClassDescUtil.descToClass("S"));
		assertEquals(String.class, ClassDescUtil.descToClass("Ljava.lang.String;"));
		assertEquals(int[][].class, ClassDescUtil.descToClass(ClassDescUtil.getDesc(int[][].class)));
		assertEquals(ClassDescTest[].class, ClassDescUtil.descToClass(ClassDescUtil.getDesc(ClassDescTest[].class)));
	}

	@Test
	void getDescTest() {
		// getDesc
		assertEquals("Z", ClassDescUtil.getDesc(boolean.class));
		assertEquals("[[[I", ClassDescUtil.getDesc(int[][][].class));
		assertEquals("[[Ljava/lang/Object;", ClassDescUtil.getDesc(Object[][].class));
	}

	@Test
	void nameToClassTest() {
		Class<?> aClass = ClassDescUtil.nameToClass("java.lang.Object[]", true, null);
		assertEquals(Object[].class, aClass);

		aClass = ClassDescUtil.nameToClass("java.lang.Object", true, null);
		assertEquals(Object.class, aClass);
	}

	@Test
	void descToNameTest() {
		assertEquals("short[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(short[].class)));
		assertEquals("boolean[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(boolean[].class)));
		assertEquals("byte[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(byte[].class)));
		assertEquals("char[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(char[].class)));
		assertEquals("double[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(double[].class)));
		assertEquals("float[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(float[].class)));
		assertEquals("int[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(int[].class)));
		assertEquals("long[]", ClassDescUtil.descToName(ClassDescUtil.getDesc(long[].class)));
		assertEquals("int", ClassDescUtil.descToName(ClassDescUtil.getDesc(int.class)));
		assertEquals("void", ClassDescUtil.descToName(ClassDescUtil.getDesc(void.class)));
		assertEquals("java.lang.Object[][]", ClassDescUtil.descToName(ClassDescUtil.getDesc(Object[][].class)));
	}

	@Test
	void nameToDescTest() {
		// name2desc
		assertEquals("Z", ClassDescUtil.nameToDesc(ClassDescUtil.getName(boolean.class)));
		assertEquals("[[[I", ClassDescUtil.nameToDesc(ClassDescUtil.getName(int[][][].class)));
		assertEquals("[[Ljava/lang/Object;",
			ClassDescUtil.nameToDesc(ClassDescUtil.getName(Object[][].class)));
	}

	@Test
	@SneakyThrows
	public void testGetDescriptor() {
		// methods
		Assertions.assertEquals("()I", ClassDescUtil.getDesc(
			Object.class.getMethod("hashCode"), false));
		Assertions.assertEquals("()Ljava/lang/String;", ClassDescUtil.getDesc(
			Object.class.getMethod("toString"), false));
		Assertions.assertEquals("(Ljava/lang/Object;)Z", ClassDescUtil.getDesc(
			Object.class.getMethod("equals", Object.class), false));
		Assertions.assertEquals("(II)I", ClassDescUtil.getDesc(
			Integer.class.getDeclaredMethod("compare", int.class, int.class), false));
		Assertions.assertEquals("([Ljava/lang/Object;)Ljava/util/List;", ClassDescUtil.getDesc(
			Arrays.class.getMethod("asList", Object[].class), false));
		Assertions.assertEquals("()V", ClassDescUtil.getDesc(
			Object.class.getConstructor(), false));

		// clazz
		Assertions.assertEquals("Z", ClassDescUtil.getDesc(boolean.class));
		Assertions.assertEquals("Ljava/lang/Boolean;", ClassDescUtil.getDesc(Boolean.class));
		Assertions.assertEquals("[[[D", ClassDescUtil.getDesc(double[][][].class));
		Assertions.assertEquals("I", ClassDescUtil.getDesc(int.class));
		Assertions.assertEquals("Ljava/lang/Integer;", ClassDescUtil.getDesc(Integer.class));
		Assertions.assertEquals("V", ClassDescUtil.getDesc(void.class));
		Assertions.assertEquals("Ljava/lang/Void;", ClassDescUtil.getDesc(Void.class));
		Assertions.assertEquals("Ljava/lang/Object;", ClassDescUtil.getDesc(Object.class));
	}
}
