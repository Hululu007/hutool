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

package org.dromara.hutool.db.ds.bee;

import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.db.config.ConnectionConfig;
import org.dromara.hutool.db.ds.AbstractDSFactory;
import org.dromara.hutool.setting.props.Props;
import org.stone.beecp.BeeDataSource;
import org.stone.beecp.BeeDataSourceConfig;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * BeeCP数据源工厂类
 *
 * @author Looly
 */
public class BeeDSFactory extends AbstractDSFactory {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 */
	public BeeDSFactory() {
		super(BeeDataSource.class, "BeeCP");
	}

	@Override
	public DataSource createDataSource(final ConnectionConfig<?> config) {
		final BeeDataSourceConfig beeConfig = new BeeDataSourceConfig(
			config.getDriver(), config.getUrl(), config.getUser(), config.getPass());

		// 连接池和其它选项
		Props.of(config.getPoolProps()).toBean(beeConfig);

		// 连接配置
		final Properties connProps = config.getConnProps();
		if(MapUtil.isNotEmpty(connProps)){
			connProps.forEach((key, value)->beeConfig.addConnectProperty(key.toString(), value));
		}

		return new BeeDataSource(beeConfig);
	}
}
