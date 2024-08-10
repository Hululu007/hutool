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

package org.dromara.hutool.poi.excel.reader;

import org.dromara.hutool.poi.excel.ExcelUtil;
import org.dromara.hutool.poi.excel.cell.CellEditor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.List;

public class CellEditorTest {

	@org.junit.jupiter.api.Test
	public void readTest(){
		final ExcelReader excelReader= ExcelUtil.getReader("cell_editor_test.xlsx");
		excelReader.getConfig().setCellEditor(new ExcelHandler());
		final List<Test> excelReaderObjects=excelReader.readAll(Test.class);

		Assertions.assertEquals("0", excelReaderObjects.get(0).getTest1());
		Assertions.assertEquals("b", excelReaderObjects.get(0).getTest2());
		Assertions.assertEquals("0", excelReaderObjects.get(1).getTest1());
		Assertions.assertEquals("b1", excelReaderObjects.get(1).getTest2());
		Assertions.assertEquals("0", excelReaderObjects.get(2).getTest1());
		Assertions.assertEquals("c2", excelReaderObjects.get(2).getTest2());
	}

	@AllArgsConstructor
	@Data
	public static class Test implements Serializable {
		private static final long serialVersionUID = 1L;

		private String test1;
		private String test2;
	}

	public static class ExcelHandler implements CellEditor {
		@ Override
		public Object edit(final Cell cell, Object o) {
			if (cell.getColumnIndex()==0 && cell.getRowIndex() != 0){
				o="0";
			}
			return o;
		}
	}
}
