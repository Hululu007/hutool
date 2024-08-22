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

package org.dromara.hutool.http.server;

import org.dromara.hutool.core.util.CharsetUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

import java.io.Closeable;
import java.nio.charset.Charset;

/**
 * HttpServer公用对象，提供HttpExchange包装和公用方法
 *
 * @author looly
 * @since 5.2.6
 */
public class HttpServerBase implements Closeable {

	final static Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;

	final HttpExchange httpExchange;

	/**
	 * 构造
	 *
	 * @param httpExchange {@link HttpExchange}
	 */
	public HttpServerBase(final HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
	}

	/**
	 * 获取{@link HttpExchange}对象
	 *
	 * @return {@link HttpExchange}对象
	 */
	public HttpExchange getHttpExchange() {
		return this.httpExchange;
	}

	/**
	 * 获取{@link HttpContext}
	 *
	 * @return {@link HttpContext}
	 * @since 5.5.7
	 */
	public HttpContext getHttpContext() {
		return getHttpExchange().getHttpContext();
	}

	/**
	 * 调用{@link HttpExchange#close()}，关闭请求流和响应流
	 */
	@Override
	public void close() {
		this.httpExchange.close();
	}
}
