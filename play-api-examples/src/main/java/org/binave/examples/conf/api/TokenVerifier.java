package org.binave.examples.conf.api;

/**
 * token 验证接口
 * 由数据模块实现
 *
 * todo 现在暂时由配置模块实现
 *
 * @author by bin jin on 2017/5/13.
 * @since 1.8
 */
public interface TokenVerifier {

    /**
     * 验证 token，成功后返回 pool，失败返回 -1
     *
     * @return  pool id
     */
    int verify(String key, String token);

}
