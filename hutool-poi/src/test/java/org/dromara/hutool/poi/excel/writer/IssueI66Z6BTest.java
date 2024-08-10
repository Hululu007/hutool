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

package org.dromara.hutool.poi.excel.writer;

import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.poi.excel.ExcelUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IssueI66Z6BTest {

	/**
	 * 部分字段定义别名后，由于IndexedComparator比较器的问题，会造成字段丢失，已修复
	 */
	@Test
	@Disabled
	public void test() {
		final File destFile = new File("D:/test/0001test.xlsx");
		FileUtil.del(destFile);

		final List<Map<String, Object>> dataList = new ArrayList<>();

		final Map<String, Object> row1 = new LinkedHashMap<>();
		row1.put("姓名", "张三");
		row1.put("年龄", 23);
		row1.put("成绩", 88.32);
		row1.put("是否合格", true);
		row1.put("考试日期", DateUtil.now());
		dataList.add(row1);

		final Map<String, Object> row2 = new LinkedHashMap<>();
		row2.put("姓名", "李四");
		row2.put("年龄", 33);
		row2.put("成绩", 59.50);
		row2.put("是否合格", false);
		row2.put("考试日期", DateUtil.now());
		dataList.add(row2);

		// 通过工具类创建writer
		final ExcelWriter writer = ExcelUtil.getWriter(destFile);

		//自定义标题别名
		final ExcelWriteConfig config = writer.getConfig();
		config.addHeaderAlias("姓名", "name");
		config.addHeaderAlias("年龄", "age");

		writer.write(dataList, true);
		writer.close();
	}
}
