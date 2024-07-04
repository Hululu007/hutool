package org.dromara.hutool.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Issue3619Test {
	@Test
	public void parseObjTest() {
		final String json = "{\"@timestamp\":\"2024-06-14T00:02:06.438Z\",\"@version\":\"1\",\"int_arr\":[-4]}";
		final JSONConfig jsonConfig = JSONConfig.of().setKeyComparator(String.CASE_INSENSITIVE_ORDER);
		final JSONObject jsonObject = JSONUtil.parseObj(json, jsonConfig);

		final String jsonStr = jsonObject.toJSONString(0, pair -> {
			final Object key = pair.getKey();
			if(key instanceof String){
				// 只有key为String时才检查并过滤，其它类型的key，如int类型的key跳过
				return key.toString().equals("int_arr");
			}else{
				return true;
			}
		});

		Assertions.assertEquals("{\"int_arr\":[-4]}", jsonStr);
	}
}