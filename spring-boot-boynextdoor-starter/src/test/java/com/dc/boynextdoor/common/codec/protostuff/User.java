package com.dc.boynextdoor.common.codec.protostuff;

import lombok.Data;

/**
 * User
 *
 * @title User
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-30
 **/
@Data
public class User {

    private String username;

    private String password;

    private Integer age;

    private String sex;

}
