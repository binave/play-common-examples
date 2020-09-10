package org.binave.examples.data.args;

import lombok.*;
import org.binave.play.data.args.DaoEditor;

/**
 * PlayerSource 接口的返回值
 * @author by bin jin on 2017/2/26.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Player extends DaoEditor {

    private long id;

    private int pool;

    private String nikeName;

    private int money;

    private int maxHP;

    private int x;

    private int y;

    @Override
    public Object[] getParams() {
        return new Object[]{id, pool, nikeName, money, maxHP};
    }

}
