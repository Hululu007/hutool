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

package org.dromara.hutool.http.client.engine.httpclient5;

import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;
import org.dromara.hutool.http.client.body.BytesBody;
import org.dromara.hutool.http.client.body.HttpBody;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link HttpBody}转换为{@link org.apache.hc.core5.http.HttpEntity}对象
 *
 * @author looly
 * @since 6.0.0
 */
public class HttpClient5BodyEntity extends AbstractHttpEntity {

	private final HttpBody body;

	/**
	 * 构造
	 *
	 * @param contentType Content-Type类型
	 * @param contentEncoding 压缩媒体类型，如gzip、deflate等
	 * @param chunked 是否块模式传输
	 * @param body {@link HttpBody}
	 */
	public HttpClient5BodyEntity(final String contentType, final String contentEncoding, final boolean chunked, final HttpBody body) {
		super(contentType, contentEncoding, chunked);
		this.body = body;
	}

	@Override
	public void writeTo(final OutputStream outStream) {
		if(null != body){
			body.writeClose(outStream);
		}
	}

	@Override
	public InputStream getContent() {
		return body.getStream();
	}

	@Override
	public boolean isStreaming() {
		return body instanceof BytesBody;
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public long getContentLength() {
		return body.contentLength();
	}
}
