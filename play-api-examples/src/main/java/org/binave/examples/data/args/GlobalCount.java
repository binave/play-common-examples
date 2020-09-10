package org.binave.examples.data.args;

import lombok.*;
import org.binave.play.data.args.DaoEditor;

/**
 *
 * 全局配置
 *
 * @author by bin jin on 2017/4/24.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GlobalCount extends DaoEditor {

    private long id;

    private int pool;

    private int count;

    private String description;

    @Override
    public Object[] getParams() {
        return new Object[]{id, pool, count, description};
    }

}
