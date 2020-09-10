package org.binave.examples.conf.args;

import java.util.List;

/**
 * 合成接口
 *
 * @author by bin jin on 2017/3/27.
 */
public interface Craft {

    // 获取合成所需道具（数量）列表
    List<Grid<?>> getCraftList();

}
