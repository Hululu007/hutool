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

package org.dromara.hutool.core.convert;

import org.dromara.hutool.core.map.MapBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map转换单元测试
 *
 * @author looly
 *
 */
public class MapConvertTest {

	@Test
	public void beanToMapTest() {
		final User user = new User();
		user.setName("AAA");
		user.setAge(45);

		final HashMap<?, ?> map = Convert.convert(HashMap.class, user);
		Assertions.assertEquals("AAA", map.get("name"));
		Assertions.assertEquals(45, map.get("age"));
	}

	@Test
	public void mapToMapTest() {
		final Map<String, Object> srcMap = MapBuilder
				.of(new HashMap<String, Object>())
				.put("name", "AAA")
				.put("age", 45).map();

		final LinkedHashMap<?, ?> map = Convert.convert(LinkedHashMap.class, srcMap);
		Assertions.assertEquals("AAA", map.get("name"));
		Assertions.assertEquals(45, map.get("age"));
	}

	public static class User {
		private String name;
		private int age;

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(final int age) {
			this.age = age;
		}
	}
}
