/*
 * Copyright (c) 2023 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.http;

import org.dromara.hutool.core.util.CharsetUtil;
import org.dromara.hutool.http.meta.ContentType;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ContentType 单元测试
 *
 */
public class ContentTypeTest {

	@Test
	public void testBuild() {
		final String result = ContentType.build(ContentType.JSON, CharsetUtil.UTF_8);
		assertEquals("application/json;charset=UTF-8", result);
	}

	@Test
	void testGetWithLeadingSpace() {
		final String json = " {\n" +
			"     \"name\": \"hutool\"\n" +
			" }";
		final ContentType contentType = ContentType.get(json);
		assertEquals(ContentType.JSON, contentType);
	}
}
