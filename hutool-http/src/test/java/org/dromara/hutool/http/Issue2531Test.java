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

package org.dromara.hutool.http;

import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.net.url.UrlBuilder;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Issue2531Test {

	@Test
	@Disabled
	public void getTest(){
		final Map<String,String> map = new HashMap<>();
		map.put("str","+123");

		final String queryParam = MapUtil.join(map, "&", "=");//返回str=+123
		Console.log(queryParam);

		final Request request = Request.of("http://localhost:8888/formTest?" + queryParam);
		//request.setUrl("http://localhost:8888/formTest" + "?" + queryParam);
		//noinspection resource
		final Response execute = request.send();
		Console.log(execute.body());
	}

	@Test
	public void encodePlusTest(){
		// 根据RFC3986规范，在URL中，"+"是安全字符，所以不会解码也不会编码
		UrlBuilder builder = UrlBuilder.ofHttp("https://hutool.cn/a=+");
		Assertions.assertEquals("https://hutool.cn/a=+", builder.toString());

		// 由于+为安全字符无需编码，ofHttp方法会把%2B解码为+，但是编码的时候不会编码
		builder = UrlBuilder.ofHttp("https://hutool.cn/a=%2B");
		Assertions.assertEquals("https://hutool.cn/a=+", builder.toString());

		// 如果你不想解码%2B，则charset传null表示不做解码，编码时候也被忽略
		builder = UrlBuilder.ofHttp("https://hutool.cn/a=%2B", null);
		Assertions.assertEquals("https://hutool.cn/a=%2B", builder.toString());
	}
}
