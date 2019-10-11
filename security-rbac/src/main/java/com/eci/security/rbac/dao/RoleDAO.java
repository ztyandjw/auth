package com.eci.security.rbac.dao;

import com.eci.security.rbac.common.dataobject.RoleDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */

@Repository
public interface RoleDAO {

    List<RoleDO> getRolesByUseridAndAppid(@Param("userid") Long userid, @Param("appid") Long appid);

}
