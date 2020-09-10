package org.binave.examples.config;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.binave.common.serialize.Codec;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用 ProtoStuff 实现的 Codec
 * 无法处理接口类型、抽象类型（包含数组类型）
 *
 * @author by bin jin on 2017/4/20.
 */
@Deprecated
public class ProtoStuffCodec implements Codec {

    @Override
    public <POJO> byte[] encode(POJO pojo) {
        if (pojo == null) return null;
        Schema schema = getSchema(pojo.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtostuffIOUtil.toByteArray(pojo, schema, buffer);
    }

    @Override
    public <POJO> POJO decode(byte[] bytes, Class<POJO> type, Class<?>... generics) {
        // todo
        if (bytes == null || type == null) return null;
        Schema<POJO> schema = getSchema(type);
        POJO pojo = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, pojo, schema);
        return pojo;
    }

    @Override
    public <POJO> POJO copy(POJO pojo) {
        if (pojo == null) return null;
        Schema<POJO> schema = getSchema((Class<POJO>) pojo.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] body = ProtostuffIOUtil.toByteArray(pojo, schema, buffer);
        pojo = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(body, pojo, schema);
        return pojo;
    }

    private Map<Class, Schema> schemaMap = new HashMap<>();

    private <POJO> Schema<POJO> getSchema(Class<POJO> type) {
        Schema<POJO> schema = schemaMap.get(type);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(type);
            schemaMap.put(type, schema);
        }
        return schema;

    }

}
