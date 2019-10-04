package com.dc.boynextdoor.common.codec.protostuff;

import com.alibaba.fastjson.JSON;
import com.dc.boynextdoor.common.codec.Codec;
import org.junit.Test;

/**
 * ProtostuffCodecTest
 *
 * @title ProtostuffCodecTest
 * @Description 验证API字段不兼容的情况
 * @Author donglongcheng01
 * @Date 2019-09-30
 **/
public class ProtostuffCodecTest {

    @Test
    public void testCodec() {
        try {
            Codec codec = new ProtostuffCodec();
            User user = new User();
            user.setUsername("dlc");
            user.setPassword("niubi");
            user.setSex("male");
            user.setAge(27);

            byte[] content = codec.encode(User.class, user);

            User user2 = codec.decode(User.class, content);
            System.out.println(JSON.toJSONString(user2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCodecNotCompatible() {
        try {
            Codec codec = new ProtostuffCodec();
            User user = new User();
            user.setUsername("dlc");
            user.setPassword("niubi");
            user.setSex("male");
            user.setAge(27);

            byte[] content = codec.encode(User.class, user);

            User2 user2 = codec.decode(User2.class, content);
            System.out.println(JSON.toJSONString(user2));

        } catch (Exception e) {
            // api不兼容的时候会抛RuntimeException异常
            e.printStackTrace();
        }
    }


}
