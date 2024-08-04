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

package org.dromara.hutool.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JSONNullTest {

	@Test
	public void parseNullTest(){
		final JSONObject bodyjson = JSONUtil.parseObj("{\n" +
				"            \"device_model\": null,\n" +
				"            \"device_status_date\": null,\n" +
				"            \"imsi\": null,\n" +
				"            \"act_date\": \"2021-07-23T06:23:26.000+00:00\"}");
		Assertions.assertNull(bodyjson.get("device_model"));
		Assertions.assertNull(bodyjson.get("device_status_date"));
		Assertions.assertNull(bodyjson.get("imsi"));

		bodyjson.config().setIgnoreNullValue(true);
		Assertions.assertEquals("{\"act_date\":\"2021-07-23T06:23:26.000+00:00\"}", bodyjson.toString());
	}

	@Test
	public void parseNullTest2(){
		final JSONObject bodyjson = JSONUtil.parseObj("{\n" +
				"            \"device_model\": null,\n" +
				"            \"device_status_date\": null,\n" +
				"            \"imsi\": null,\n" +
				"            \"act_date\": \"2021-07-23T06:23:26.000+00:00\"}", true);
		Assertions.assertFalse(bodyjson.containsKey("device_model"));
		Assertions.assertFalse(bodyjson.containsKey("device_status_date"));
		Assertions.assertFalse(bodyjson.containsKey("imsi"));
	}

	@Test
	public void setNullTest(){
		// 忽略null
		String json1 = JSONUtil.ofObj().set("key1", null).toString();
		Assertions.assertEquals("{}", json1);

		// 不忽略null
		json1 = JSONUtil.ofObj(JSONConfig.of().setIgnoreNullValue(false)).set("key1", null).toString();
		Assertions.assertEquals("{\"key1\":null}", json1);
	}

	@Test
	public void setNullOfJSONArrayTest(){
		// 忽略null
		String json1 = JSONUtil.ofArray().set(null).toString();
		Assertions.assertEquals("[]", json1);

		// 不忽略null
		json1 = JSONUtil.ofArray(JSONConfig.of().setIgnoreNullValue(false)).set(null).toString();
		Assertions.assertEquals("[null]", json1);
	}
}
