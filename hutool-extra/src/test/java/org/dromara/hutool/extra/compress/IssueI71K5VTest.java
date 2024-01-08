/*
 * Copyright (c) 2023 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.extra.compress;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class IssueI71K5VTest {

	@SuppressWarnings("resource")
	@Test
	@Disabled
	void createArchiverTest() {
		CompressUtil.
			createArchiver(CharsetUtil.UTF_8, ArchiveStreamFactory.ZIP,
				FileUtil.file("d:\\test\\test.zip"))
			.add(FileUtil.file("d:\\java\\"))
			.close();
	}
}