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

package org.dromara.hutool.json.engine;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.lang.wrapper.SimpleWrapper;
import org.dromara.hutool.json.JSONException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Jackson引擎
 *
 * @author Looly
 */
public class JacksonEngine extends SimpleWrapper<ObjectMapper> implements JSONEngine{

	/**
	 * 构造
	 */
	public JacksonEngine() {
		super(new ObjectMapper());
		// 允许出现单引号
		raw.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 允许没有引号的字段名(非标准)
		raw.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	@Override
	public void serialize(final Object bean, final Writer writer) {
		try {
			raw.writeValue(writer, bean);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(final Reader reader, final Object type) {
		try {
			if(type instanceof Class){
				return raw.readValue(reader, (Class<T>)type);
			} else if(type instanceof TypeReference){
				return raw.readValue(reader, (TypeReference<T>)type);
			} else if(type instanceof JavaType){
				return raw.readValue(reader, (JavaType)type);
			}
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		throw new JSONException("Unsupported type: {}", type.getClass());
	}
}