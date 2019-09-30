package com.dc.boynextdoor.common.codec.protostuff;

import com.dc.boynextdoor.common.codec.Codec;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * <p>【核心】通过{@code protostuff}来序列化和反序列化字节数组
 * <p>[关于protostuff](https://github.com/protostuff/protostuff)
 * <p>http://javadox.com/io.protostuff/protostuff-runtime/1.3.8/io/protostuff/runtime/RuntimeEnv.html
 *
 * @title ProtostuffCodec
 * @Description 通过{@code protostuff}来序列化和反序列化字节数组
 * @Author donglongcheng01
 * @Date 2019-09-26
 **/
public class ProtostuffCodec implements Codec {

    public static final String NAME = "protostuff";

    static {
        // 对于List/Collection这样的字段，序列化这个List/Collection对象，而不是只序列化value
        System.setProperty("protostuff.runtime.collection_schema_on_repeated_fields", "true");
        // 也是对于List/Collection，记录下它的实现方式（链表or线性表）
        System.setProperty("protostuff.runtime.morph_collection_interfaces", "true");
        // 对于Map，记录实现类（hash or treeMap）
        System.setProperty("protostuff.runtime.morph_map_interfaces", "true");
    }

    // 每个线程一个buffer，用来复用缓冲区来序列化写入--》网络远端
    private ThreadLocal<LinkedBuffer> linkedBuffer =
            ThreadLocal.withInitial(() -> LinkedBuffer.allocate(500));


    /**
     * 反序列化字节数组->对象
     *
     * @param clazz 需要反序列化的类，用以得到schema
     * @param bytes 远端传来的字节数组
     * @param <T> 类
     * @return T的实例
     * @throws Exception api不兼容问题
     */
    @Override
    public <T> T decode(Class<T> clazz, byte[] bytes) throws Exception {
        // 通过class得到schema
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T content = clazz.newInstance();
        // 根据schema解析bytes到content，todo api不兼容问题发生在这里
        ProtostuffIOUtil.mergeFrom(bytes, content, schema);
        return content;
    }

    /**
     * 序列化Object为字节数组然后传给远端，linkedBuffer缓冲用来复用
     *
     * @param clazz 类信息
     * @param object 需要被序列化的对象
     * @param <T> 类
     * @return 序列化完的字节数组
     * @throws Exception 序列化失败
     */
    @Override
    public <T> byte[] encode(Class<T> clazz, T object) throws Exception {
        try {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(object, schema, linkedBuffer.get());
        } finally {
            linkedBuffer.get().clear();
        }
    }
}
