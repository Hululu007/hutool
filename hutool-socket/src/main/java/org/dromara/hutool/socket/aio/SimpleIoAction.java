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

package org.dromara.hutool.socket.aio;

import java.nio.ByteBuffer;

import org.dromara.hutool.log.LogUtil;

/**
 * 简易IO信息处理类<br>
 * 简单实现了accept和failed事件
 *
 * @author looly
 *
 */
public abstract class SimpleIoAction implements IoAction<ByteBuffer> {

	@Override
	public void accept(final AioSession session) {
	}

	@Override
	public void failed(final Throwable exc, final AioSession session) {
		LogUtil.error(exc);
	}
}
