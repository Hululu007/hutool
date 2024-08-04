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

package org.dromara.hutool.core.date.format.parser;

import org.dromara.hutool.core.date.DatePattern;
import org.dromara.hutool.core.date.DateTime;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.regex.PatternPool;
import org.dromara.hutool.core.regex.ReUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.io.Serializable;

/**
 * 时间日期字符串，日期默认为当天，支持格式类似于；
 * <pre>
 *   HH:mm:ss
 *   HH:mm
 * </pre>
 *
 * @author looly
 * @since 6.0.0
 */
public class TimeParser implements PredicateDateParser, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单例
	 */
	public static final TimeParser INSTANCE = new TimeParser();

	@Override
	public boolean test(final CharSequence dateStr) {
		return ReUtil.isMatch(PatternPool.TIME, dateStr);
	}

	@Override
	public DateTime parse(CharSequence source) {
		// issue#I9C2D4 处理时分秒
		//15时45分59秒 修正为 15:45:59
		source = StrUtil.replaceChars(source, "时分秒", ":");

		source = StrUtil.format("{} {}", DateUtil.formatToday(), source);
		if (1 == StrUtil.count(source, ':')) {
			// 时间格式为 HH:mm
			return new DateTime(source, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
		} else {
			// 时间格式为 HH:mm:ss
			return new DateTime(source, DatePattern.NORM_DATETIME_FORMAT);
		}
	}
}
