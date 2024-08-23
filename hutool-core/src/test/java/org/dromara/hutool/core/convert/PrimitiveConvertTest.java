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

import org.dromara.hutool.core.convert.impl.PrimitiveConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveConvertTest {

	@Test
	public void toIntTest(){
		final int convert = ConvertUtil.convert(int.class, "123");
		Assertions.assertEquals(123, convert);
	}

	@Test
	public void toIntErrorTest(){
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			ConvertUtil.convert(int.class, "aaaa");
		});
	}

	@Test
	public void toIntValueTest() {
		final Object a = PrimitiveConverter.INSTANCE.convert(int.class, null);
		Assertions.assertNull(a);
	}
}
