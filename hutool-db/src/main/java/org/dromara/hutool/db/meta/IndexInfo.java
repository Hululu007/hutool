/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.db.meta;


import org.dromara.hutool.core.util.ObjUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库表的索引信息<br>
 * 如果时单列索引，只有一个{@link ColumnIndexInfo}，联合索引则拥有多个{@link ColumnIndexInfo}
 *
 * @author huzhongying
 */
public class IndexInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * 索引值是否可以不唯一
	 */
	private boolean nonUnique;

	/**
	 * 索引名称
	 */
	private String indexName;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * table所在的schema
	 */
	private String schema;
	/**
	 * table所在的catalog
	 */
	private String catalog;

	/**
	 * 索引中的列信息,按索引顺序排列
	 */
	private List<ColumnIndexInfo> columnIndexInfoList;

	/**
	 * 构造
	 *
	 * @param nonUnique 索引值是否可以不唯一
	 * @param indexName 索引名称
	 * @param tableName 表名
	 * @param schema    table所在的schema
	 * @param catalog   table所在的catalog
	 */
	public IndexInfo(final boolean nonUnique, final String indexName, final String tableName, final String schema, final String catalog) {
		this.nonUnique = nonUnique;
		this.indexName = indexName;
		this.tableName = tableName;
		this.schema = schema;
		this.catalog = catalog;
		this.setColumnIndexInfoList(new ArrayList<>());
	}

	public boolean isNonUnique() {
		return nonUnique;
	}

	public void setNonUnique(final boolean nonUnique) {
		this.nonUnique = nonUnique;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(final String indexName) {
		this.indexName = indexName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(final String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	public List<ColumnIndexInfo> getColumnIndexInfoList() {
		return columnIndexInfoList;
	}

	public void setColumnIndexInfoList(final List<ColumnIndexInfo> columnIndexInfoList) {
		this.columnIndexInfoList = columnIndexInfoList;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final IndexInfo indexInfo = (IndexInfo) o;
		return ObjUtil.equals(indexName, indexInfo.indexName)
				&& ObjUtil.equals(tableName, indexInfo.tableName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(indexName, tableName);
	}

	@Override
	public IndexInfo clone() throws CloneNotSupportedException {
		return (IndexInfo) super.clone();
	}

	@Override
	public String toString() {
		return "IndexInfo{" +
				"nonUnique=" + nonUnique +
				", indexName='" + indexName + '\'' +
				", tableName='" + tableName + '\'' +
				", schema='" + schema + '\'' +
				", catalog='" + catalog + '\'' +
				", columnIndexInfoList=" + columnIndexInfoList +
				'}';
	}
}
