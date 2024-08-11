/*
 * Copyright (c) 2024. looly(loolly@aliyun.com)
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

import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.*;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.map.TableMap;
import org.dromara.hutool.core.map.concurrent.SafeConcurrentHashMap;
import org.dromara.hutool.core.map.multi.RowKeyTable;
import org.dromara.hutool.core.map.multi.Table;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.poi.excel.*;
import org.dromara.hutool.poi.excel.cell.CellRangeUtil;
import org.dromara.hutool.poi.excel.cell.CellUtil;
import org.dromara.hutool.poi.excel.cell.editors.CellEditor;
import org.dromara.hutool.poi.excel.style.*;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Excel 写入器<br>
 * 此工具用于通过POI将数据写出到Excel，此对象可完成以下两个功能
 *
 * <pre>
 * 1. 编辑已存在的Excel，可写出原Excel文件，也可写出到其它地方（到文件或到流）
 * 2. 新建一个空的Excel工作簿，完成数据填充后写出（到文件或到流）
 * </pre>
 *
 * @author Looly
 * @since 3.2.0
 */
@SuppressWarnings("resource")
public class ExcelWriter extends ExcelBase<ExcelWriter, ExcelWriteConfig> {

	/**
	 * 样式集，定义不同类型数据样式
	 */
	private StyleSet styleSet;
	/**
	 * 标题项对应列号缓存，每次写标题更新此缓存
	 */
	private Map<String, Integer> headLocationCache;
	/**
	 * 当前行
	 */
	private final AtomicInteger currentRow;

	// region Constructors

	/**
	 * 构造，默认生成xlsx格式的Excel文件<br>
	 * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
	 * 若写出到文件，还需调用{@link #setDestFile(File)}方法自定义写出的文件，然后调用{@link #flush()}方法写出到文件
	 *
	 * @since 3.2.1
	 */
	public ExcelWriter() {
		this(true);
	}

	/**
	 * 构造<br>
	 * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
	 * 若写出到文件，需要调用{@link #flush(File)} 写出到文件
	 *
	 * @param isXlsx 是否为xlsx格式
	 * @since 3.2.1
	 */
	public ExcelWriter(final boolean isXlsx) {
		this(WorkbookUtil.createBook(isXlsx), null);
	}

	/**
	 * 构造，默认写出到第一个sheet，第一个sheet名为sheet1
	 *
	 * @param destFilePath 目标文件路径，可以不存在
	 */
	public ExcelWriter(final String destFilePath) {
		this(destFilePath, null);
	}

	/**
	 * 构造<br>
	 * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
	 * 若写出到文件，需要调用{@link #flush(File)} 写出到文件
	 *
	 * @param isXlsx    是否为xlsx格式
	 * @param sheetName sheet名，第一个sheet名并写出到此sheet，例如sheet1
	 * @since 4.1.8
	 */
	public ExcelWriter(final boolean isXlsx, final String sheetName) {
		this(WorkbookUtil.createBook(isXlsx), sheetName);
	}

	/**
	 * 构造
	 *
	 * @param destFilePath 目标文件路径，可以不存在
	 * @param sheetName    sheet名，第一个sheet名并写出到此sheet，例如sheet1
	 */
	public ExcelWriter(final String destFilePath, final String sheetName) {
		this(FileUtil.file(destFilePath), sheetName);
	}

	/**
	 * 构造，默认写出到第一个sheet，第一个sheet名为sheet1
	 *
	 * @param destFile 目标文件，可以不存在
	 */
	public ExcelWriter(final File destFile) {
		this(destFile, null);
	}

	/**
	 * 构造
	 *
	 * @param destFile  目标文件，可以不存在
	 * @param sheetName sheet名，做为第一个sheet名并写出到此sheet，例如sheet1
	 */
	public ExcelWriter(final File destFile, final String sheetName) {
		this(WorkbookUtil.createBookForWriter(destFile), sheetName);
		this.targetFile = destFile;
	}

	/**
	 * 构造<br>
	 * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
	 * 若写出到文件，还需调用{@link #setDestFile(File)}方法自定义写出的文件，然后调用{@link #flush()}方法写出到文件
	 *
	 * @param workbook  {@link Workbook}
	 * @param sheetName sheet名，做为第一个sheet名并写出到此sheet，例如sheet1
	 */
	public ExcelWriter(final Workbook workbook, final String sheetName) {
		this(SheetUtil.getOrCreateSheet(workbook, sheetName));
	}

	/**
	 * 构造<br>
	 * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
	 * 若写出到文件，还需调用{@link #setDestFile(File)}方法自定义写出的文件，然后调用{@link #flush()}方法写出到文件
	 *
	 * @param sheet {@link Sheet}
	 * @since 4.0.6
	 */
	public ExcelWriter(final Sheet sheet) {
		super(new ExcelWriteConfig(), sheet);
		this.styleSet = new DefaultStyleSet(workbook);
		this.currentRow = new AtomicInteger(0);
	}
	// endregion


	@Override
	public ExcelWriter setConfig(final ExcelWriteConfig config) {
		return super.setConfig(config);
	}

	@Override
	public ExcelWriter setSheet(final int sheetIndex) {
		// 切换到新sheet需要重置开始行
		reset();
		return super.setSheet(sheetIndex);
	}

	@Override
	public ExcelWriter setSheet(final String sheetName) {
		// 切换到新sheet需要重置开始行
		reset();
		return super.setSheet(sheetName);
	}

	/**
	 * 重置Writer，包括：
	 *
	 * <pre>
	 * 1. 当前行游标归零
	 * 2. 清空别名比较器
	 * 3. 清除标题缓存
	 * </pre>
	 *
	 * @return this
	 */
	public ExcelWriter reset() {
		resetRow();
		return this;
	}

	/**
	 * 重命名当前sheet
	 *
	 * @param sheetName 新的sheet名
	 * @return this
	 * @since 4.1.8
	 */
	public ExcelWriter renameSheet(final String sheetName) {
		return renameSheet(this.workbook.getSheetIndex(this.sheet), sheetName);
	}

	/**
	 * 重命名sheet
	 *
	 * @param sheet     sheet序号，0表示第一个sheet
	 * @param sheetName 新的sheet名
	 * @return this
	 * @since 4.1.8
	 */
	public ExcelWriter renameSheet(final int sheet, final String sheetName) {
		this.workbook.setSheetName(sheet, sheetName);
		return this;
	}

	/**
	 * 设置所有列为自动宽度，不考虑合并单元格<br>
	 * 此方法必须在指定列数据完全写出后调用才有效。<br>
	 * 列数计算是通过第一行计算的
	 *
	 * @param useMergedCells 是否适用于合并单元格
	 * @param widthRatio     列宽的倍数。如果所有内容都是英文，可以设为1，如果有中文，建议设置为 1.6-2.0之间。
	 * @return this
	 * @since 4.0.12
	 */
	public ExcelWriter autoSizeColumnAll(final boolean useMergedCells, final float widthRatio) {
		final int columnCount = this.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			autoSizeColumn(i, useMergedCells, widthRatio);
		}
		return this;
	}

	/**
	 * 设置某列为自动宽度。注意有中文的情况下，需要根据需求调整宽度扩大比例。<br>
	 * 此方法必须在指定列数据完全写出后调用才有效。
	 *
	 * @param columnIndex    第几列，从0计数
	 * @param useMergedCells 是否适用于合并单元格
	 * @param widthRatio     列宽的倍数。如果所有内容都是英文，可以设为1，如果有中文，建议设置为 1.6-2.0之间。
	 * @return this
	 * @since 5.8.30
	 */
	public ExcelWriter autoSizeColumn(final int columnIndex, final boolean useMergedCells, final float widthRatio) {
		if (widthRatio > 0) {
			sheet.setColumnWidth(columnIndex, (int) (sheet.getColumnWidth(columnIndex) * widthRatio));
		} else {
			sheet.autoSizeColumn(columnIndex, useMergedCells);
		}
		return this;
	}

	/**
	 * 禁用默认样式
	 *
	 * @return this
	 * @see #setStyleSet(StyleSet)
	 * @since 4.6.3
	 */
	public ExcelWriter disableDefaultStyle() {
		return setStyleSet(null);
	}

	/**
	 * 设置样式集，如果不使用样式，传入{@code null}
	 *
	 * @param styleSet 样式集，{@code null}表示无样式
	 * @return this
	 * @since 4.1.11
	 */
	public ExcelWriter setStyleSet(final StyleSet styleSet) {
		this.styleSet = styleSet;
		return this;
	}

	/**
	 * 获取样式集，样式集可以自定义包括：<br>
	 *
	 * <pre>
	 * 1. 头部样式
	 * 2. 一般单元格样式
	 * 3. 默认数字样式
	 * 4. 默认日期样式
	 * </pre>
	 *
	 * @return 样式集
	 * @since 4.0.0
	 */
	public StyleSet getStyleSet() {
		return this.styleSet;
	}

	/**
	 * 获得当前行
	 *
	 * @return 当前行
	 */
	public int getCurrentRow() {
		return this.currentRow.get();
	}

	/**
	 * 设置当前所在行
	 *
	 * @param rowIndex 行号
	 * @return this
	 */
	public ExcelWriter setCurrentRow(final int rowIndex) {
		this.currentRow.set(rowIndex);
		return this;
	}

	/**
	 * 定位到最后一行的后边，用于追加数据
	 *
	 * @return this
	 * @since 5.5.0
	 */
	public ExcelWriter setCurrentRowToEnd() {
		return setCurrentRow(getRowCount());
	}

	/**
	 * 跳过当前行
	 *
	 * @return this
	 */
	public ExcelWriter passCurrentRow() {
		this.currentRow.incrementAndGet();
		return this;
	}

	/**
	 * 跳过指定行数
	 *
	 * @param rows 跳过的行数
	 * @return this
	 */
	public ExcelWriter passRows(final int rows) {
		this.currentRow.addAndGet(rows);
		return this;
	}

	/**
	 * 重置当前行为0
	 *
	 * @return this
	 */
	public ExcelWriter resetRow() {
		this.currentRow.set(0);
		return this;
	}

	/**
	 * 设置写出的目标文件
	 *
	 * @param destFile 目标文件
	 * @return this
	 */
	public ExcelWriter setDestFile(final File destFile) {
		this.targetFile = destFile;
		return this;
	}

	//region header alias
	//endregion

	/**
	 * 设置窗口冻结，之前冻结的窗口会被覆盖，如果rowSplit为0表示取消冻结
	 *
	 * @param rowSplit 冻结的行及行数，2表示前两行
	 * @return this
	 * @since 5.2.5
	 */
	public ExcelWriter setFreezePane(final int rowSplit) {
		return setFreezePane(0, rowSplit);
	}

	/**
	 * 设置窗口冻结，之前冻结的窗口会被覆盖，如果colSplit和rowSplit为0表示取消冻结
	 *
	 * @param colSplit 冻结的列及列数，2表示前两列
	 * @param rowSplit 冻结的行及行数，2表示前两行
	 * @return this
	 * @since 5.2.5
	 */
	public ExcelWriter setFreezePane(final int colSplit, final int rowSplit) {
		getSheet().createFreezePane(colSplit, rowSplit);
		return this;
	}

	/**
	 * 设置列宽（单位为一个字符的宽度，例如传入width为10，表示10个字符的宽度）
	 *
	 * @param columnIndex 列号（从0开始计数，-1表示所有列的默认宽度）
	 * @param width       宽度（单位1~255个字符宽度）
	 * @return this
	 * @since 4.0.8
	 */
	public ExcelWriter setColumnWidth(final int columnIndex, final int width) {
		if (columnIndex < 0) {
			this.sheet.setDefaultColumnWidth(width);
		} else {
			this.sheet.setColumnWidth(columnIndex, width * 256);
		}
		return this;
	}

	/**
	 * 设置默认行高，值为一个点的高度
	 *
	 * @param height 高度
	 * @return this
	 * @since 4.6.5
	 */
	public ExcelWriter setDefaultRowHeight(final int height) {
		return setRowHeight(-1, height);
	}

	/**
	 * 设置行高，值为一个点的高度
	 *
	 * @param rownum 行号（从0开始计数，-1表示所有行的默认高度）
	 * @param height 高度
	 * @return this
	 * @since 4.0.8
	 */
	public ExcelWriter setRowHeight(final int rownum, final int height) {
		if (rownum < 0) {
			this.sheet.setDefaultRowHeightInPoints(height);
		} else {
			final Row row = this.sheet.getRow(rownum);
			if (null != row) {
				row.setHeightInPoints(height);
			}
		}
		return this;
	}

	/**
	 * 设置Excel页眉或页脚
	 *
	 * @param text     页脚的文本
	 * @param align    对齐方式枚举 {@link Align}
	 * @param isFooter 是否为页脚，false表示页眉，true表示页脚
	 * @return this
	 * @since 4.1.0
	 */
	public ExcelWriter setHeaderOrFooter(final String text, final Align align, final boolean isFooter) {
		final HeaderFooter headerFooter = isFooter ? this.sheet.getFooter() : this.sheet.getHeader();
		switch (align) {
			case LEFT:
				headerFooter.setLeft(text);
				break;
			case RIGHT:
				headerFooter.setRight(text);
				break;
			case CENTER:
				headerFooter.setCenter(text);
				break;
			default:
				break;
		}
		return this;
	}

	/**
	 * 设置忽略错误，即Excel中的绿色警告小标，只支持XSSFSheet和SXSSFSheet<br>
	 * 见：https://stackoverflow.com/questions/23488221/how-to-remove-warning-in-excel-using-apache-poi-in-java
	 *
	 * @param cellRangeAddress  指定单元格范围
	 * @param ignoredErrorTypes 忽略的错误类型列表
	 * @return this
	 * @throws UnsupportedOperationException 如果sheet不是XSSFSheet
	 * @since 5.8.28
	 */
	public ExcelWriter addIgnoredErrors(final CellRangeAddress cellRangeAddress, final IgnoredErrorType... ignoredErrorTypes) throws UnsupportedOperationException {
		final Sheet sheet = this.sheet;
		if (sheet instanceof XSSFSheet) {
			((XSSFSheet) sheet).addIgnoredErrors(cellRangeAddress, ignoredErrorTypes);
			return this;
		} else if (sheet instanceof SXSSFSheet) {
			// SXSSFSheet并未提供忽略错误方法，获得其内部_sh字段设置
			final XSSFSheet xssfSheet = (XSSFSheet) FieldUtil.getFieldValue(sheet, "_sh");
			if (null != xssfSheet) {
				xssfSheet.addIgnoredErrors(cellRangeAddress, ignoredErrorTypes);
			}
		}

		throw new UnsupportedOperationException("Only XSSFSheet supports addIgnoredErrors");
	}

	/**
	 * 增加下拉列表
	 *
	 * @param x          x坐标，列号，从0开始
	 * @param y          y坐标，行号，从0开始
	 * @param selectList 下拉列表
	 * @return this
	 * @since 4.6.2
	 */
	public ExcelWriter addSelect(final int x, final int y, final String... selectList) {
		return addSelect(new CellRangeAddressList(y, y, x, x), selectList);
	}

	/**
	 * 增加下拉列表
	 *
	 * @param regions    {@link CellRangeAddressList} 指定下拉列表所占的单元格范围
	 * @param selectList 下拉列表内容
	 * @return this
	 * @since 4.6.2
	 */
	public ExcelWriter addSelect(final CellRangeAddressList regions, final String... selectList) {
		final DataValidationHelper validationHelper = this.sheet.getDataValidationHelper();
		final DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(selectList);

		//设置下拉框数据
		final DataValidation dataValidation = validationHelper.createValidation(constraint, regions);

		//处理Excel兼容性问题
		if (dataValidation instanceof XSSFDataValidation) {
			dataValidation.setSuppressDropDownArrow(true);
			dataValidation.setShowErrorBox(true);
		} else {
			dataValidation.setSuppressDropDownArrow(false);
		}

		return addValidationData(dataValidation);
	}

	/**
	 * 增加单元格控制，比如下拉列表、日期验证、数字范围验证等
	 *
	 * @param dataValidation {@link DataValidation}
	 * @return this
	 * @since 4.6.2
	 */
	public ExcelWriter addValidationData(final DataValidation dataValidation) {
		this.sheet.addValidationData(dataValidation);
		return this;
	}

	/**
	 * 合并当前行的单元格
	 *
	 * @param lastColumn 合并到的最后一个列号
	 * @return this
	 */
	public ExcelWriter merge(final int lastColumn) {
		return merge(lastColumn, null);
	}

	/**
	 * 合并当前行的单元格，并写入对象到单元格<br>
	 * 如果写到单元格中的内容非null，行号自动+1，否则当前行号不变
	 *
	 * @param lastColumn 合并到的最后一个列号
	 * @param content    合并单元格后的内容
	 * @return this
	 */
	public ExcelWriter merge(final int lastColumn, final Object content) {
		return merge(lastColumn, content, true);
	}

	/**
	 * 合并某行的单元格，并写入对象到单元格<br>
	 * 如果写到单元格中的内容非null，行号自动+1，否则当前行号不变
	 *
	 * @param lastColumn       合并到的最后一个列号
	 * @param content          合并单元格后的内容
	 * @param isSetHeaderStyle 是否为合并后的单元格设置默认标题样式，只提取边框样式
	 * @return this
	 * @since 4.0.10
	 */
	public ExcelWriter merge(final int lastColumn, final Object content, final boolean isSetHeaderStyle) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");

		final int rowIndex = this.currentRow.get();
		merge(CellRangeUtil.ofSingleRow(rowIndex, lastColumn), content, isSetHeaderStyle);

		// 设置内容后跳到下一行
		if (null != content) {
			this.currentRow.incrementAndGet();
		}
		return this;
	}

	/**
	 * 合并某行的单元格，并写入对象到单元格
	 *
	 * @param cellRangeAddress 合并单元格范围，定义了起始行列和结束行列
	 * @param content          合并单元格后的内容
	 * @param isSetHeaderStyle 是否为合并后的单元格设置默认标题样式，只提取边框样式
	 * @return this
	 * @since 4.0.10
	 */
	public ExcelWriter merge(final CellRangeAddress cellRangeAddress, final Object content, final boolean isSetHeaderStyle) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");

		CellStyle style = null;
		if (null != this.styleSet) {
			style = styleSet.getStyleFor(new CellReference(cellRangeAddress.getFirstRow(), cellRangeAddress.getFirstColumn()), content, isSetHeaderStyle);
		}

		return merge(cellRangeAddress, content, style);
	}

	/**
	 * 合并单元格，并写入对象到单元格,使用指定的样式<br>
	 * 指定样式传入null，则不使用任何样式
	 *
	 * @param cellRangeAddress 合并单元格范围，定义了起始行列和结束行列
	 * @param content          合并单元格后的内容
	 * @param cellStyle        合并后单元格使用的样式，可以为null
	 * @return this
	 * @since 5.6.5
	 */
	public ExcelWriter merge(final CellRangeAddress cellRangeAddress, final Object content, final CellStyle cellStyle) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");

		CellUtil.mergingCells(this.getSheet(), cellRangeAddress, cellStyle);

		// 设置内容
		if (null != content) {
			final Cell cell = getOrCreateCell(cellRangeAddress.getFirstColumn(), cellRangeAddress.getFirstRow());
			CellUtil.setCellValue(cell, content, cellStyle, this.config.getCellEditor());
		}
		return this;
	}

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加
	 * 默认的，当当前行号为0时，写出标题（如果为Map或Bean），否则不写标题
	 *
	 * <p>
	 * data中元素支持的类型有：
	 *
	 * <pre>
	 * 1. Iterable，即元素为一个集合，元素被当作一行，data表示多行<br>
	 * 2. Map，即元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
	 * 3. Bean，即元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
	 * 4. 其它类型，按照基本类型输出（例如字符串）
	 * </pre>
	 *
	 * @param data 数据
	 * @return this
	 */
	public ExcelWriter write(final Iterable<?> data) {
		return write(data, 0 == getCurrentRow());
	}

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加
	 *
	 * <p>
	 * data中元素支持的类型有：
	 *
	 * <pre>
	 * 1. Iterable，即元素为一个集合，元素被当作一行，data表示多行<br>
	 * 2. Map，即元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
	 * 3. Bean，即元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
	 * 4. 其它类型，按照基本类型输出（例如字符串）
	 * </pre>
	 *
	 * @param data             数据
	 * @param isWriteKeyAsHead 是否强制写出标题行（Map或Bean）
	 * @return this
	 */
	public ExcelWriter write(final Iterable<?> data, final boolean isWriteKeyAsHead) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		boolean isFirst = true;
		for (final Object object : data) {
			writeRow(object, isFirst && isWriteKeyAsHead);
			if (isFirst) {
				isFirst = false;
			}
		}
		return this;
	}

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加
	 * data中元素支持的类型有：
	 *
	 * <p>
	 * 1. Map，即元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
	 * 2. Bean，即元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
	 * </p>
	 *
	 * @param data       数据
	 * @param comparator 比较器，用于字段名的排序
	 * @return this
	 * @since 3.2.3
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public ExcelWriter write(final Iterable<?> data, final Comparator<String> comparator) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		boolean isFirstRow = true;
		Map<?, ?> map;
		for (final Object obj : data) {
			if (obj instanceof Map) {
				map = new TreeMap<>(comparator);
				map.putAll((Map) obj);
			} else {
				map = BeanUtil.beanToMap(obj, new TreeMap<>(comparator), false, false);
			}
			writeRow(map, isFirstRow);
			if (isFirstRow) {
				isFirstRow = false;
			}
		}
		return this;
	}

	// region ----- writeImg

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 添加图片到当前sheet中 / 默认图片类型png / 默认的起始坐标和结束坐标都为0
	 *
	 * @param imgFile 图片文件
	 * @param col1    指定起始的列，下标从0开始
	 * @param row1    指定起始的行，下标从0开始
	 * @param col2    指定结束的列，下标从0开始
	 * @param row2    指定结束的行，下标从0开始
	 * @return this
	 * @author vhukze
	 * @since 5.7.18
	 */
	public ExcelWriter writeImg(final File imgFile, final int col1, final int row1, final int col2, final int row2) {
		return writeImg(imgFile, new SimpleClientAnchor(col1, row1, col2, row2));
	}

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 添加图片到当前sheet中 / 默认图片类型png
	 *
	 * @param imgFile      图片文件
	 * @param clientAnchor 图片的位置和大小信息
	 * @return this
	 * @author vhukze
	 * @since 6.0.0
	 */
	public ExcelWriter writeImg(final File imgFile, final SimpleClientAnchor clientAnchor) {
		return writeImg(imgFile, ExcelImgUtil.getImgType(imgFile), clientAnchor);
	}

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 添加图片到当前sheet中
	 *
	 * @param imgFile      图片文件
	 * @param imgType      图片类型，对应poi中Workbook类中的图片类型2-7变量
	 * @param clientAnchor 图片的位置和大小信息
	 * @return this
	 * @author vhukze
	 * @since 6.0.0
	 */
	public ExcelWriter writeImg(final File imgFile, final int imgType, final SimpleClientAnchor clientAnchor) {
		return writeImg(FileUtil.readBytes(imgFile), imgType, clientAnchor);
	}

	/**
	 * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 添加图片到当前sheet中
	 *
	 * @param pictureData  数据bytes
	 * @param imgType      图片类型，对应poi中Workbook类中的图片类型2-7变量
	 * @param clientAnchor 图片的位置和大小信息
	 * @return this
	 * @author vhukze
	 * @since 6.0.0
	 */
	public ExcelWriter writeImg(final byte[] pictureData, final int imgType, final SimpleClientAnchor clientAnchor) {
		final Drawing<?> patriarch = this.sheet.createDrawingPatriarch();
		final ClientAnchor anchor = this.workbook.getCreationHelper().createClientAnchor();
		clientAnchor.copyTo(anchor);

		patriarch.createPicture(anchor, this.workbook.addPicture(pictureData, imgType));
		return this;
	}

	/**
	 * 绘制线条
	 *
	 * @param clientAnchor 绘制区域信息
	 * @return this
	 * @since 6.0.0
	 */
	public ExcelWriter writeLineShape(final SimpleClientAnchor clientAnchor) {
		return writeSimpleShape(clientAnchor, ShapeConfig.of());
	}

	/**
	 * 绘制线条
	 *
	 * @param clientAnchor 绘制区域信息
	 * @param lineStyle    线条样式
	 * @param lineWidth    线条粗细
	 * @param lineColor    线条颜色
	 * @return this
	 * @since 6.0.0
	 */
	public ExcelWriter writeLineShape(final SimpleClientAnchor clientAnchor, final LineStyle lineStyle, final int lineWidth, final Color lineColor) {
		return writeSimpleShape(clientAnchor, ShapeConfig.of().setLineStyle(lineStyle).setLineWidth(lineWidth).setLineColor(lineColor));
	}

	/**
	 * 绘制简单形状
	 *
	 * @param clientAnchor 绘制区域信息
	 * @param shapeConfig  形状配置，包括形状类型、线条样式、线条宽度、线条颜色、填充颜色等
	 * @return this
	 * @since 6.0.0
	 */
	public ExcelWriter writeSimpleShape(final SimpleClientAnchor clientAnchor, ShapeConfig shapeConfig) {
		final Drawing<?> patriarch = this.sheet.createDrawingPatriarch();
		final ClientAnchor anchor = this.workbook.getCreationHelper().createClientAnchor();
		clientAnchor.copyTo(anchor);

		if (null == shapeConfig) {
			shapeConfig = ShapeConfig.of();
		}
		final Color lineColor = shapeConfig.getLineColor();
		if (patriarch instanceof HSSFPatriarch) {
			final HSSFSimpleShape simpleShape = ((HSSFPatriarch) patriarch).createSimpleShape((HSSFClientAnchor) anchor);
			simpleShape.setShapeType(shapeConfig.getShapeType().ooxmlId);
			simpleShape.setLineStyle(shapeConfig.getLineStyle().getValue());
			simpleShape.setLineWidth(shapeConfig.getLineWidth());
			if (null != lineColor) {
				simpleShape.setLineStyleColor(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue());
			}
		} else if (patriarch instanceof XSSFDrawing) {
			final XSSFSimpleShape simpleShape = ((XSSFDrawing) patriarch).createSimpleShape((XSSFClientAnchor) anchor);
			simpleShape.setShapeType(shapeConfig.getShapeType().ooxmlId);
			simpleShape.setLineStyle(shapeConfig.getLineStyle().getValue());
			simpleShape.setLineWidth(shapeConfig.getLineWidth());
			if (null != lineColor) {
				simpleShape.setLineStyleColor(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue());
			}
		} else {
			throw new UnsupportedOperationException("Unsupported patriarch type: " + patriarch.getClass().getName());
		}
		return this;
	}
	// endregion

	// region ----- writeRow

	/**
	 * 写出一行标题数据<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * @param rowData 一行的数据
	 * @return this
	 */
	public ExcelWriter writeHeadRow(final Iterable<?> rowData) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		this.headLocationCache = new SafeConcurrentHashMap<>();
		final Row row = this.sheet.createRow(this.currentRow.getAndIncrement());
		final CellEditor cellEditor = this.config.getCellEditor();
		int i = 0;
		Cell cell;
		for (final Object value : rowData) {
			cell = row.createCell(i);
			CellUtil.setCellValue(cell, value, this.styleSet, true, cellEditor);
			this.headLocationCache.put(StrUtil.toString(value), i);
			i++;
		}
		return this;
	}

	/**
	 * 写出复杂标题的第二行标题数据<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * <p>
	 * 此方法的逻辑是：将一行数据写出到当前行，遇到已存在的单元格跳过，不存在的创建并赋值。
	 * </p>
	 *
	 * @param rowData 一行的数据
	 * @return this
	 */
	public ExcelWriter writeSecHeadRow(final Iterable<?> rowData) {
		final Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
		final Iterator<?> iterator = rowData.iterator();
		//如果获取的row存在单元格，则执行复杂表头逻辑，否则直接调用writeHeadRow(Iterable<?> rowData)
		if (row.getLastCellNum() != 0) {
			final CellEditor cellEditor = this.config.getCellEditor();
			for (int i = 0; i < this.workbook.getSpreadsheetVersion().getMaxColumns(); i++) {
				Cell cell = row.getCell(i);
				if (cell != null) {
					continue;
				}
				if (iterator.hasNext()) {
					cell = row.createCell(i);
					CellUtil.setCellValue(cell, iterator.next(), this.styleSet, true, cellEditor);
				} else {
					break;
				}
			}
		} else {
			writeHeadRow(rowData);
		}
		return this;
	}

	/**
	 * 写出一行，根据rowBean数据类型不同，写出情况如下：
	 *
	 * <pre>
	 * 1、如果为Iterable，直接写出一行
	 * 2、如果为Map，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
	 * 3、如果为Bean，转为Map写出，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
	 * </pre>
	 *
	 * @param rowBean          写出的Bean
	 * @param isWriteKeyAsHead 为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
	 * @return this
	 * @see #writeRow(Iterable)
	 * @see #writeRow(Map, boolean)
	 * @since 4.1.5
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public ExcelWriter writeRow(final Object rowBean, final boolean isWriteKeyAsHead) {
		final ExcelWriteConfig config = this.config;

		final Map rowMap;
		if (rowBean instanceof Map) {
			if (MapUtil.isNotEmpty(config.getHeaderAlias())) {
				rowMap = MapUtil.newTreeMap((Map) rowBean, config.getCachedAliasComparator());
			} else {
				rowMap = (Map) rowBean;
			}
		} else if (rowBean instanceof Iterable) {
			// issue#2398@Github
			// MapWrapper由于实现了Iterable接口，应该优先按照Map处理
			return writeRow((Iterable<?>) rowBean);
		} else if (rowBean instanceof Hyperlink) {
			// Hyperlink当成一个值
			return writeRow(ListUtil.of(rowBean), isWriteKeyAsHead);
		} else if (BeanUtil.isReadableBean(rowBean.getClass())) {
			if (MapUtil.isEmpty(config.getHeaderAlias())) {
				rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap<>(), false, false);
			} else {
				// 别名存在情况下按照别名的添加顺序排序Bean数据
				rowMap = BeanUtil.beanToMap(rowBean, new TreeMap<>(config.getCachedAliasComparator()), false, false);
			}
		} else {
			// 其它转为字符串默认输出
			return writeRow(ListUtil.of(rowBean), isWriteKeyAsHead);
		}
		return writeRow(rowMap, isWriteKeyAsHead);
	}

	/**
	 * 将一个Map写入到Excel，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values<br>
	 * 如果rowMap为空（包括null），则写出空行
	 *
	 * @param rowMap           写出的Map，为空（包括null），则写出空行
	 * @param isWriteKeyAsHead 为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
	 * @return this
	 */
	public ExcelWriter writeRow(final Map<?, ?> rowMap, final boolean isWriteKeyAsHead) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		if (MapUtil.isEmpty(rowMap)) {
			// 如果写出数据为null或空，跳过当前行
			return passCurrentRow();
		}

		final Table<?, ?, ?> aliasTable = aliasTable(rowMap);
		if (isWriteKeyAsHead) {
			// 写出标题行，并记录标题别名和列号的关系
			writeHeadRow(aliasTable.columnKeys());
			// 记录原数据key对应列号
			int i = 0;
			for (final Object key : aliasTable.rowKeySet()) {
				this.headLocationCache.putIfAbsent(StrUtil.toString(key), i);
				i++;
			}
		}

		// 如果已经写出标题行，根据标题行找对应的值写入
		if (MapUtil.isNotEmpty(this.headLocationCache)) {
			final Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
			final CellEditor cellEditor = this.config.getCellEditor();
			Integer location;
			for (final Table.Cell<?, ?, ?> cell : aliasTable) {
				// 首先查找原名对应的列号
				location = this.headLocationCache.get(StrUtil.toString(cell.getRowKey()));
				if (null == location) {
					// 未找到，则查找别名对应的列号
					location = this.headLocationCache.get(StrUtil.toString(cell.getColumnKey()));
				}
				if (null != location) {
					CellUtil.setCellValue(CellUtil.getOrCreateCell(row, location), cell.getValue(), this.styleSet, false, cellEditor);
				}
			}
		} else {
			writeRow(aliasTable.values());
		}
		return this;
	}

	/**
	 * 写出一行数据<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * @param rowData 一行的数据
	 * @return this
	 */
	public ExcelWriter writeRow(final Iterable<?> rowData) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		RowUtil.writeRow(this.sheet.createRow(this.currentRow.getAndIncrement()), rowData, this.styleSet, false, this.config.getCellEditor());
		return this;
	}
	// endregion

	// region ----- writeCol

	/**
	 * 从第1列开始按列写入数据(index 从0开始)<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * @param colMap           一列的数据
	 * @param isWriteKeyAsHead 是否将Map的Key作为表头输出，如果为True第一行为表头，紧接着为values
	 * @return this
	 */
	public ExcelWriter writeCol(final Map<?, ? extends Iterable<?>> colMap, final boolean isWriteKeyAsHead) {
		return writeCol(colMap, 0, isWriteKeyAsHead);
	}

	/**
	 * 从指定列开始按列写入数据(index 从0开始)<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * @param colMap           一列的数据
	 * @param startColIndex    起始的列号，从0开始
	 * @param isWriteKeyAsHead 是否将Map的Key作为表头输出，如果为True第一行为表头，紧接着为values
	 * @return this
	 */
	public ExcelWriter writeCol(final Map<?, ? extends Iterable<?>> colMap, int startColIndex, final boolean isWriteKeyAsHead) {
		for (final Object k : colMap.keySet()) {
			final Iterable<?> v = colMap.get(k);
			if (v != null) {
				writeCol(isWriteKeyAsHead ? k : null, startColIndex, v, startColIndex != colMap.size() - 1);
				startColIndex++;
			}
		}
		return this;
	}


	/**
	 * 为第一列写入数据<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * @param headerVal       表头名称,如果为null则不写入
	 * @param colData         需要写入的列数据
	 * @param isResetRowIndex 如果为true，写入完毕后Row index 将会重置为写入之前的未知，如果为false，写入完毕后Row index将会在写完的数据下方
	 * @return this
	 */
	public ExcelWriter writeCol(final Object headerVal, final Iterable<?> colData, final boolean isResetRowIndex) {
		return writeCol(headerVal, 0, colData, isResetRowIndex);
	}

	/**
	 * 为第指定列写入数据<br>
	 * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
	 * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1
	 *
	 * @param headerVal       表头名称,如果为null则不写入
	 * @param colIndex        列index
	 * @param colData         需要写入的列数据
	 * @param isResetRowIndex 如果为true，写入完毕后Row index 将会重置为写入之前的未知，如果为false，写入完毕后Row index将会在写完的数据下方
	 * @return this
	 */
	public ExcelWriter writeCol(final Object headerVal, final int colIndex, final Iterable<?> colData, final boolean isResetRowIndex) {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		int currentRowIndex = currentRow.get();
		if (null != headerVal) {
			writeCellValue(colIndex, currentRowIndex, headerVal, true);
			currentRowIndex++;
		}
		for (final Object colDatum : colData) {
			writeCellValue(colIndex, currentRowIndex, colDatum);
			currentRowIndex++;
		}
		if (!isResetRowIndex) {
			currentRow.set(currentRowIndex);
		}
		return this;
	}
	// endregion

	// region ----- writeCellValue

	/**
	 * 给指定单元格赋值，使用默认单元格样式
	 *
	 * @param locationRef 单元格地址标识符，例如A11，B5
	 * @param value       值
	 * @return this
	 * @since 5.1.4
	 */
	public ExcelWriter writeCellValue(final String locationRef, final Object value) {
		final CellReference cellReference = new CellReference(locationRef);
		return writeCellValue(cellReference.getCol(), cellReference.getRow(), value);
	}

	/**
	 * 给指定单元格赋值，使用默认单元格样式
	 *
	 * @param x     X坐标，从0计数，即列号
	 * @param y     Y坐标，从0计数，即行号
	 * @param value 值
	 * @return this
	 * @since 4.0.2
	 */
	public ExcelWriter writeCellValue(final int x, final int y, final Object value) {
		return writeCellValue(x, y, value, false);
	}

	/**
	 * 给指定单元格赋值，使用默认单元格样式
	 *
	 * @param x        X坐标，从0计数，即列号
	 * @param y        Y坐标，从0计数，即行号
	 * @param isHeader 是否为Header
	 * @param value    值
	 * @return this
	 */
	public ExcelWriter writeCellValue(final int x, final int y, final Object value, final boolean isHeader) {
		final Cell cell = getOrCreateCell(x, y);
		CellUtil.setCellValue(cell, value, this.styleSet, isHeader, this.config.getCellEditor());
		return this;
	}
	// endregion

	// region ----- setStyle

	/**
	 * 设置某个单元格的样式<br>
	 * 此方法用于多个单元格共享样式的情况<br>
	 * 可以调用{@link #getOrCreateCellStyle(int, int)} 方法创建或取得一个样式对象。
	 *
	 * <p>
	 * 需要注意的是，共享样式会共享同一个{@link CellStyle}，一个单元格样式改变，全部改变。
	 *
	 * @param style       单元格样式
	 * @param locationRef 单元格地址标识符，例如A11，B5
	 * @return this
	 * @since 5.1.4
	 */
	public ExcelWriter setStyle(final CellStyle style, final String locationRef) {
		final CellReference cellReference = new CellReference(locationRef);
		return setStyle(style, cellReference.getCol(), cellReference.getRow());
	}

	/**
	 * 设置某个单元格的样式<br>
	 * 此方法用于多个单元格共享样式的情况<br>
	 * 可以调用{@link #getOrCreateCellStyle(int, int)} 方法创建或取得一个样式对象。
	 *
	 * <p>
	 * 需要注意的是，共享样式会共享同一个{@link CellStyle}，一个单元格样式改变，全部改变。
	 *
	 * @param style 单元格样式
	 * @param x     X坐标，从0计数，即列号
	 * @param y     Y坐标，从0计数，即行号
	 * @return this
	 * @since 4.6.3
	 */
	public ExcelWriter setStyle(final CellStyle style, final int x, final int y) {
		final Cell cell = getOrCreateCell(x, y);
		cell.setCellStyle(style);
		return this;
	}

	/**
	 * 设置行样式
	 *
	 * @param y     Y坐标，从0计数，即行号
	 * @param style 样式
	 * @return this
	 * @see Row#setRowStyle(CellStyle)
	 * @since 5.4.5
	 */
	public ExcelWriter setRowStyle(final int y, final CellStyle style) {
		getOrCreateRow(y).setRowStyle(style);
		return this;
	}

	/**
	 * 对数据行整行加自定义样式 仅对数据单元格设置 write后调用
	 * <p>
	 * {@link ExcelWriter#setRowStyle(int, org.apache.poi.ss.usermodel.CellStyle)}
	 * 这个方法加的样式会使整行没有数据的单元格也有样式
	 * 特别是加背景色时很不美观 且有数据的单元格样式会被StyleSet中的样式覆盖掉
	 *
	 * @param y     行坐标
	 * @param style 自定义的样式
	 * @return this
	 * @since 5.7.3
	 */
	public ExcelWriter setRowStyleIfHasData(final int y, final CellStyle style) {
		if (y < 0) {
			throw new IllegalArgumentException("Invalid row number (" + y + ")");
		}
		final int columnCount = this.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.setStyle(style, i, y);
		}
		return this;
	}

	/**
	 * 设置列的默认样式
	 *
	 * @param x     列号，从0开始
	 * @param style 样式
	 * @return this
	 * @since 5.6.4
	 */
	public ExcelWriter setColumnStyle(final int x, final CellStyle style) {
		this.sheet.setDefaultColumnStyle(x, style);
		return this;
	}

	/**
	 * 设置整个列的样式 仅对数据单元格设置 write后调用
	 * <p>
	 * {@link ExcelWriter#setColumnStyle(int, org.apache.poi.ss.usermodel.CellStyle)}
	 * 这个方法加的样式会使整列没有数据的单元格也有样式
	 * 特别是加背景色时很不美观 且有数据的单元格样式会被StyleSet中的样式覆盖掉
	 *
	 * @param x     列的索引
	 * @param y     起始行
	 * @param style 样式
	 * @return this
	 * @since 5.7.3
	 */
	public ExcelWriter setColumnStyleIfHasData(final int x, final int y, final CellStyle style) {
		if (x < 0) {
			throw new IllegalArgumentException("Invalid column number (" + x + ")");
		}
		if (y < 0) {
			throw new IllegalArgumentException("Invalid row number (" + y + ")");
		}
		final int rowCount = this.getRowCount();
		for (int i = y; i < rowCount; i++) {
			this.setStyle(style, x, i);
		}
		return this;
	}
	// endregion

	// region ----- flush

	/**
	 * 将Excel Workbook刷出到预定义的文件<br>
	 * 如果用户未自定义输出的文件，将抛出{@link NullPointerException}<br>
	 * 预定义文件可以通过{@link #setDestFile(File)} 方法预定义，或者通过构造定义
	 *
	 * @return this
	 * @throws IORuntimeException IO异常
	 */
	public ExcelWriter flush() throws IORuntimeException {
		return flush(this.targetFile);
	}

	/**
	 * 将Excel Workbook刷出到文件<br>
	 * 如果用户未自定义输出的文件，将抛出{@link NullPointerException}
	 *
	 * @param destFile 写出到的文件
	 * @return this
	 * @throws IORuntimeException IO异常
	 * @since 4.0.6
	 */
	public ExcelWriter flush(final File destFile) throws IORuntimeException {
		Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).");
		return flush(FileUtil.getOutputStream(destFile), true);
	}

	/**
	 * 将Excel Workbook刷出到输出流
	 *
	 * @param out 输出流
	 * @return this
	 * @throws IORuntimeException IO异常
	 */
	public ExcelWriter flush(final OutputStream out) throws IORuntimeException {
		return flush(out, false);
	}

	/**
	 * 将Excel Workbook刷出到输出流
	 *
	 * @param out        输出流
	 * @param isCloseOut 是否关闭输出流
	 * @return this
	 * @throws IORuntimeException IO异常
	 * @since 4.4.1
	 */
	public ExcelWriter flush(final OutputStream out, final boolean isCloseOut) throws IORuntimeException {
		Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
		try {
			this.workbook.write(out);
			out.flush();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		} finally {
			if (isCloseOut) {
				IoUtil.closeQuietly(out);
			}
		}
		return this;
	}
	// endregion

	/**
	 * 关闭工作簿<br>
	 * 如果用户设定了目标文件，先写出目标文件后给关闭工作簿
	 */
	@Override
	public void close() {
		if (null != this.targetFile) {
			flush();
		}
		closeWithoutFlush();
	}

	/**
	 * 关闭工作簿但是不写出
	 */
	protected void closeWithoutFlush() {
		super.close();
		this.currentRow.set(0);

		// 清空对象
		this.styleSet = null;
	}

	// region ----- Private method start

	/**
	 * 为指定的key列表添加标题别名，如果没有定义key的别名，在onlyAlias为false时使用原key<br>
	 * key为别名，value为字段值
	 *
	 * @param rowMap 一行数据
	 * @return 别名列表
	 */
	private Table<?, ?, ?> aliasTable(final Map<?, ?> rowMap) {
		final Table<Object, Object, Object> filteredTable = new RowKeyTable<>(new LinkedHashMap<>(), TableMap::new);
		final Map<String, String> headerAlias = this.config.getHeaderAlias();
		final boolean onlyAlias = this.config.onlyAlias;
		if (MapUtil.isEmpty(headerAlias)) {
			rowMap.forEach((key, value) -> filteredTable.put(key, key, value));
		} else {
			rowMap.forEach((key, value) -> {
				final String aliasName = headerAlias.get(StrUtil.toString(key));
				if (null != aliasName) {
					// 别名键值对加入
					filteredTable.put(key, aliasName, value);
				} else if (!onlyAlias) {
					// 保留无别名设置的键值对
					filteredTable.put(key, key, value);
				}
			});
		}

		return filteredTable;
	}
	// endregion
}
