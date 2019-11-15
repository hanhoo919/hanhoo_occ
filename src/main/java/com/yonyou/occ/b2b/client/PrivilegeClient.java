package com.yonyou.occ.b2b.client;

import com.yonyou.ocm.common.privilege.PrivilegeApi;
import org.springframework.cloud.openfeign.FeignClient;

import static com.yonyou.ocm.common.client.Client.OCC_CMPT;

/**
 * 用户权限外部接口的Feign客户端。
 *
 * @author wangruiv
 * @date 2018-05-25 14:39:38
 */
@FeignClient(value = OCC_CMPT, contextId = "privilegeApi", url = "${feign.url}/occ-cmpt")
public interface PrivilegeClient extends PrivilegeApi {
}
