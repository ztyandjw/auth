package com.eci.security.rbac.common.dataobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */

@Data
public class BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date updateTime;

    private Integer deleted;

    private String createUser;

    private String updateUser;

}
