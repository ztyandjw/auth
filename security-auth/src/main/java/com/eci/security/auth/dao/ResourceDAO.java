package com.eci.security.auth.dao;

import com.eci.security.auth.common.dataobject.ResourceDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/26.
 */


@Repository
public interface ResourceDAO {

    List<ResourceDO> getResourceByRoleIds(@Param("roleIds") List<Long> roleIdList);
}
