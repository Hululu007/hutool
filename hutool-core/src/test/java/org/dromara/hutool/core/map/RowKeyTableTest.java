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

package org.dromara.hutool.core.map;

import org.dromara.hutool.core.map.multi.RowKeyTable;
import org.dromara.hutool.core.map.multi.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class RowKeyTableTest {

	@Test
	public void putGetTest(){
		final Table<Integer, Integer, Integer> table = new RowKeyTable<>();
		table.put(1, 2, 3);
		table.put(1, 6, 4);

		Assertions.assertEquals(new Integer(3), table.get(1, 2));
		Assertions.assertNull(table.get(1, 3));

		//判断row和column确定的二维点是否存在
		Assertions.assertTrue(table.contains(1, 2));
		Assertions.assertFalse(table.contains(1, 3));

		//判断列
		Assertions.assertTrue(table.containsColumn(2));
		Assertions.assertFalse(table.containsColumn(3));

		// 判断行
		Assertions.assertTrue(table.containsRow(1));
		Assertions.assertFalse(table.containsRow(2));


		// 获取列
		final Map<Integer, Integer> column = table.getColumn(6);
		Assertions.assertEquals(1, column.size());
		Assertions.assertEquals(Integer.valueOf(4), column.get(1));
	}

	@Test
	public void issue3135Test() {
		final Table<Integer, Integer, Integer> table = new RowKeyTable<>();
		table.put(1, 2, 3);
		table.put(1, 6, 4);

		Assertions.assertNull(table.getRow(2));
		Assertions.assertFalse(table.contains(2, 3));
	}
}
