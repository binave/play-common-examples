
package org.binave.examples.logic.test;

import org.binave.examples.general.impl.handler.SellGridBaseHandlerImpl;
import org.binave.examples.protoc.Msg;
import org.binave.common.util.CharUtil;
import org.binave.common.util.MonitorUtil;
import org.binave.play.protoc.BaseProtocHandler;
import org.binave.play.route.args.DataPacket;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author by bin jin on 2017/5/6.
 * @since 1.8
 */
public class Demo {

    @Test
    public void test() {

        long n = System.currentTimeMillis();

        Msg.C_SellGrid_12035.Builder builder = Msg.C_SellGrid_12035.newBuilder();

        builder.addGridList(1);
        builder.addGridList(3);
        builder.addGridList(6);
        builder.addGridList(2);
        builder.setTabId(4);

        for (int i = 0; i < 300; i++) {

            byte[] d = builder.build().toByteArray();
            time(n);

            BaseProtocHandler handler = new SellGridBaseHandlerImpl();

            DataPacket dp = handler.call(new DataPacket(1, 1, 1, 1, d));
            time(n);

            System.out.println(handler.tab() + ", " + dp);
            time(n);

            System.out.println();
        }
    }

    private int c = 0;

    private void time(long n) {
        System.out.println(
                (++c) + " - " + MonitorUtil.timeMillisFormat(System.currentTimeMillis() - n)
        );
    }

    @Test
    public void test2() {
        Random ran = new SecureRandom();

        long t = System.currentTimeMillis();
        for (int i = 0; i < 300_000; i++) {
            int k = ran.nextInt();
            if (ran.nextBoolean()) k *= -1;

            String str = "org.binave.examples.protoc.Msg$C_LoginServer_" + k + "$Builder";
            int j = CharUtil.getInteger(str, 45, 0);
            int h = CharUtil.getInteger(str, -9,-1);

            if (k != j || h != j)
                System.out.println(k + " - " + j + " - " + h);
        }
        System.out.println(
                MonitorUtil.timeMillisFormat(System.currentTimeMillis() - t)
        );
    }

}
