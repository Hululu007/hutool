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

package org.dromara.hutool.db;

import org.dromara.hutool.db.sql.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PageTest {

	@Test
	public void addOrderTest() {
		final Page page = new Page();
		page.addOrder(new Order("aaa"));
		Assertions.assertEquals(page.getOrders().length, 1);
		page.addOrder(new Order("aaa"));
		Assertions.assertEquals(page.getOrders().length, 2);
	}
}
