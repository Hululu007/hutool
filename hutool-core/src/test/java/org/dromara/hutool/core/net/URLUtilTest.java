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

package org.dromara.hutool.core.net;

import org.dromara.hutool.core.net.url.UrlUtil;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * URLUtil单元测试
 *
 * @author looly
 *
 */
public class URLUtilTest {

	@Test
	public void normalizeTest() {
		// issue#I25MZL，多个/被允许
		String url = "http://www.hutool.cn//aaa/bbb";
		String normalize = UrlUtil.normalize(url);
		assertEquals("http://www.hutool.cn//aaa/bbb", normalize);

		url = "www.hutool.cn//aaa/bbb";
		normalize = UrlUtil.normalize(url);
		assertEquals("http://www.hutool.cn//aaa/bbb", normalize);
	}

	@Test
	public void normalizeTest2() {
		String url = "http://www.hutool.cn//aaa/\\bbb?a=1&b=2";
		String normalize = UrlUtil.normalize(url);
		assertEquals("http://www.hutool.cn//aaa//bbb?a=1&b=2", normalize);

		url = "www.hutool.cn//aaa/bbb?a=1&b=2";
		normalize = UrlUtil.normalize(url);
		assertEquals("http://www.hutool.cn//aaa/bbb?a=1&b=2", normalize);
	}

	@Test
	public void normalizeTest3() {
		String url = "http://www.hutool.cn//aaa/\\bbb?a=1&b=2";
		String normalize = UrlUtil.normalize(url, true);
		assertEquals("http://www.hutool.cn//aaa//bbb?a=1&b=2", normalize);

		url = "www.hutool.cn//aaa/bbb?a=1&b=2";
		normalize = UrlUtil.normalize(url, true);
		assertEquals("http://www.hutool.cn//aaa/bbb?a=1&b=2", normalize);

		url = "\\/www.hutool.cn//aaa/bbb?a=1&b=2";
		normalize = UrlUtil.normalize(url, true);
		assertEquals("http://www.hutool.cn//aaa/bbb?a=1&b=2", normalize);
	}

	@Test
	public void normalizeIpv6Test() {
		final String url = "http://[fe80::8f8:2022:a603:d180]:9439";
		final String normalize = UrlUtil.normalize("http://[fe80::8f8:2022:a603:d180]:9439", true);
		assertEquals(url, normalize);
	}

	@Test
	public void formatTest() {
		final String url = "//www.hutool.cn//aaa/\\bbb?a=1&b=2";
		final String normalize = UrlUtil.normalize(url);
		assertEquals("http://www.hutool.cn//aaa//bbb?a=1&b=2", normalize);
	}

	@Test
	public void getHostTest() throws MalformedURLException {
		final String url = "https://www.hutool.cn//aaa/\\bbb?a=1&b=2";
		final String normalize = UrlUtil.normalize(url);
		final URI host = UrlUtil.getHost(new URL(normalize));
		assertEquals("https://www.hutool.cn", host.toString());
	}

	@Test
	public void getPathTest(){
		final String url = " http://www.aaa.bbb/search?scope=ccc&q=ddd";
		final String path = UrlUtil.getPath(url);
		assertEquals("/search", path);
	}

	@Test
	public void issue3676Test() {
		final String fileFullName = "/Uploads/20240601/aaaa.txt";
		final URI uri = UrlUtil.toURI(fileFullName);
		final URI resolve = uri.resolve(".");
		assertEquals("/Uploads/20240601/", resolve.toString());
	}
}
