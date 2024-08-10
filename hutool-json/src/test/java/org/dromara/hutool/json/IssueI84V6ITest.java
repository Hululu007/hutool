/*
 * Copyright (c) 2024. looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IssueI84V6ITest {
	@Test
	void formatTest() {
		final String a1 = "{'x':'\\n','y':','}";
		final String formatJsonStr = JSONUtil.formatJsonStr(a1);
//		Console.log(formatJsonStr);
		Assertions.assertEquals(
			"{\n" +
			"  'x': '\\n',\n" +
			"  'y': ','\n" +
			"}", formatJsonStr);
	}
}
