package org.binave.examples.data.args;

import lombok.*;
import org.binave.play.data.args.DaoEditor;

/**
 * 以数据库形式存储的 json 配置
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resource extends DaoEditor {

    // 没啥卵用的 id
    private long id;

    // 暂时写死
    private int serverId;

    // 插入时间
    private String insertTime;

    // json 名称 e.g. server.json
    private String resKey;

    // json 内容
    private String data;

    // 暂时写死
    @Override
    public int getPool() {
        return 1;
    }

    @Override
    public void setPool(int pool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] getParams() {
        return new Object[]{id, serverId, insertTime, resKey, data};
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", serverId=" + serverId +
                ", insertTime='" + insertTime + '\'' +
                ", resKey='" + resKey + '\'' +
                ", data len='" + data.length() + '\'' +
                '}';
    }
}
