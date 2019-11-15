package com.yonyou.occ.report.web;

import org.springside.modules.web.Servlets;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * 抽象controller 抽取公共方法
 */
public abstract class BaseController {

    /**
     * 从前端传入的查询条件的前缀字符串。
     */
    private static final String SearchPrefix = "search_";


    /**
     * 根据请求构造查询参数。
     *
     * @param request HTTP请求对象。
     * @return 查询参数映射。
     */
    protected Map<String, Object> buildSearchParams(HttpServletRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, SearchPrefix);
        ArrayList<Map.Entry<String, Object>> ls = new ArrayList<>();
        for (Map.Entry<String, Object> entry : searchParams.entrySet()) {
            String key = entry.getKey();
            if (key.contains("$")) {
                ls.add(entry);
            }
        }
        if (ls.size() > 0) {
            for (Map.Entry<String, Object> entry : ls) {
                String key = entry.getKey();
                String newKey = key.replace("$", ".");
                searchParams.remove(key);
                searchParams.put(newKey, entry.getValue());
            }
        }
        return searchParams;
    }

}

