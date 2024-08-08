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

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.poi.excel.ExcelUtil;
import org.dromara.hutool.poi.excel.style.DefaultStyleSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Issue2307Test {

	@Test
	@Disabled
	public void writeTest(){
		final String filePath = "d:/test/issue2307.xlsx";
		FileUtil.del(FileUtil.file(filePath));

		final List<Object> row1 = ListUtil.of("设备1", 11, 111, 1111.444, 1111.444, 1111.444, 1111.444, 119999999999999999999999999999999999999999911.444);
		final List<Object> row2 = ListUtil.of("设备2", 22, 222, 2222.555, 2222.555, 2222.555, 2222.555, 2222.555);
		final List<Object> row3 = ListUtil.of("设备3", 33, 333, 3333, 3333, 3333, 3333, 3333);
		final List<Object> row4 = ListUtil.of("设备4", 44, 444, 4444, 1, 2, 3, 4);
		final List<Object> row5 = ListUtil.of("设备5", "x1", "x2", "x3", 4, 5, 6, 7);

		final List<List<Object>> rows = ListUtil.of(row1, row2, row3, row4, row5);
		//通过工具类创建writer
		try (final ExcelWriter writer = ExcelUtil.getWriter(filePath)) {

			//合并单元格后的标题行，使用默认标题样式
			writer.merge(row1.size() - 1, "测试标题");


			//一次性写出内容，强制输出标题
			writer.write(rows, true);


			final DefaultStyleSet style = (DefaultStyleSet) writer.getStyleSet();
			final CellStyle cellStyleForNumber = style.getCellStyleForNumber();
			cellStyleForNumber.setAlignment(HorizontalAlignment.RIGHT);

			final CellStyle cellStyle = style.getCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);


			final XSSFSheet sheet = (XSSFSheet) writer.getSheet();

			for (int i = 1; i < 8; i++) {
				sheet.autoSizeColumn(i);
			}

			// 空串,实际应该有值
			Console.log("writer.getCell(1,0): " + writer.getCell(0,1));
			Console.log("writer.getCell(1,1): " + writer.getCell(1,1));
			Console.log("sheet.getRow(1).getCell(0): " + sheet.getRow(1).getCell(0));
		}
	}
}
