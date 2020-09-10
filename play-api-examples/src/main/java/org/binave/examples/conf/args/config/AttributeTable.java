package org.binave.examples.conf.args.config;


import lombok.*;
import org.binave.play.config.args.ConfigEditor;

/**
 * Map
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTable implements ConfigEditor {

    private int ID;
    private int AttributeType = 0;
    private int AttributeValue = 0;
    private int AttributeValueType = 0;
    private int BuffTriggerID = -1;

    private long version;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getKey() {
        return ID;
    }

    @Override
    public int getExtKey() {
        return -1;
    }

    @Override
    public void init() {

    }

}
