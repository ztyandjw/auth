package com.eci.security.rbac.dao;

import com.eci.security.rbac.common.dataobject.ResourceDO;
import com.eci.security.rbac.common.dataobject.RoleDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/26.
 */


@Repository
public interface ResourceDAO {

    List<ResourceDO> getResourceByRoleIdList(@Param("roleIdList") List<Long> roleIdList);
}
