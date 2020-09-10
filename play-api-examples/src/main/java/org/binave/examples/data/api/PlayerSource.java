package org.binave.examples.data.api;

import org.binave.examples.data.args.Player;
import org.binave.play.data.args.Access;

/**
 * 用户缓存
 *
 * 负责保存用户的状态
 *
 * @author by bin jin on 2017/2/26.
 */
public interface PlayerSource {

    /**
     * 从数据库中查询
     */
    Player get(Access access);

    /**
     * 更新 player 到缓存
     */
    boolean update(Player player);

}
