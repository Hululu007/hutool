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

package org.dromara.hutool.json.writer;

import org.dromara.hutool.core.convert.Converter;
import org.dromara.hutool.json.JSONConfig;
import org.dromara.hutool.json.JSONUtil;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GlobalValueWritersTest {

	@BeforeEach
	public void init(){
		GlobalValueWriters.add(new JSONValueWriter() {
			@Override
			public void write(final JSONWriter writer, final Object value) {
				writer.writeRaw(String.valueOf(((CustomSubBean)value).getId()));
			}

			@Override
			public boolean test(final Object value) {
				return value instanceof CustomSubBean;
			}
		});
	}

	@Test
	public void customWriteTest(){
		final CustomSubBean customBean = new CustomSubBean();
		customBean.setId(12);
		customBean.setName("aaa");
		final String s = JSONUtil.toJsonStr(customBean);
		Assertions.assertEquals("12", s);
	}

	@Test
	public void customWriteSubTest(){
		final CustomSubBean customSubBean = new CustomSubBean();
		customSubBean.setId(12);
		customSubBean.setName("aaa");
		final CustomBean customBean = new CustomBean();
		customBean.setId(1);
		customBean.setSub(customSubBean);

		final String s = JSONUtil.toJsonStr(customBean);
		Assertions.assertEquals("{\"id\":1,\"sub\":12}", s);

		// 自定义转换
		final JSONConfig jsonConfig = JSONConfig.of();
		final Converter converter = jsonConfig.getConverter();
		jsonConfig.setConverter((targetType, value) -> {
			if(targetType == CustomSubBean.class){
				final CustomSubBean subBean = new CustomSubBean();
				subBean.setId((Integer) value);
				return subBean;
			}
			return converter.convert(targetType, value);
		});
		final CustomBean customBean1 = JSONUtil.parseObj(s, jsonConfig).toBean(CustomBean.class);
		Assertions.assertEquals(12, customBean1.getSub().getId());
	}

	@Data
	static class CustomSubBean {
		private int id;
		private String name;
	}

	@Data
	static class CustomBean{
		private int id;
		private CustomSubBean sub;
	}
}
