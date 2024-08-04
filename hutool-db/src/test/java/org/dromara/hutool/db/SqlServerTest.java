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

import org.dromara.hutool.core.lang.Console;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * SQL Server操作单元测试
 *
 * @author looly
 *
 */
public class SqlServerTest {

	@Test
	@Disabled
	public void createTableTest() {
		Db.of("sqlserver").execute("create table T_USER(ID bigint, name varchar(255))");
	}

	@Test
	@Disabled
	public void insertTest() {
		for (int id = 100; id < 200; id++) {
			Db.of("sqlserver").insert(Entity.of("T_USER")//
					.set("ID", id)//
					.set("name", "测试用户" + id)//
			);
		}
	}

	@Test
	@Disabled
	public void pageTest() {
		final PageResult<Entity> result = Db.of("sqlserver").page(Entity.of("T_USER"), new Page(2, 10));
		for (final Entity entity : result) {
			Console.log(entity.get("ID"));
		}
	}

}
