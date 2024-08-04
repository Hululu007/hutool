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

package org.dromara.hutool.extra.compress.archiver;

import java.io.Closeable;
import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 数据归档封装，归档即将几个文件或目录打成一个压缩包
 *
 * @author looly
 */
public interface Archiver extends Closeable {

	/**
	 * 将文件或目录加入归档，目录采取递归读取方式按照层级加入
	 *
	 * @param file 文件或目录
	 * @return this
	 */
	default Archiver add(final File file) {
		return add(file, null);
	}

	/**
	 * 将文件或目录加入归档，目录采取递归读取方式按照层级加入
	 *
	 * @param file   文件或目录
	 * @param predicate 文件过滤器，指定哪些文件或目录可以加入，{@link Predicate#test(Object)}为{@code true}时加入，null表示全部加入
	 * @return this
	 */
	default Archiver add(final File file, final Predicate<File> predicate) {
		return add(file, null, predicate);
	}

	/**
	 * 将文件或目录加入归档包，目录采取递归读取方式按照层级加入
	 *
	 * @param file   文件或目录
	 * @param path   文件或目录的初始路径，null表示位于根路径
	 * @param filter 文件过滤器，指定哪些文件或目录可以加入，{@link Predicate#test(Object)}为{@code true}保留，null表示全部加入
	 * @return this
	 */
	default Archiver add(final File file, final String path, final Predicate<File> filter){
		return add(file, path, Function.identity(), filter);
	}

	/**
	 * 将文件或目录加入归档包，目录采取递归读取方式按照层级加入
	 *
	 * @param file   文件或目录
	 * @param path   文件或目录的初始路径，null表示位于根路径
	 * @param fileNameEditor 文件名编辑器
	 * @param filter 文件过滤器，指定哪些文件或目录可以加入，{@link Predicate#test(Object)}为{@code true}保留，null表示全部加入
	 * @return this
	 * @since 6.0.0
	 */
	Archiver add(File file, String path, Function<String, String> fileNameEditor, Predicate<File> filter);

	/**
	 * 结束已经增加的文件归档，此方法不会关闭归档流，可以继续添加文件
	 *
	 * @return this
	 */
	Archiver finish();

	/**
	 * 无异常关闭
	 */
	@Override
	void close();
}
