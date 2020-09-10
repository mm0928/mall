package com.leyou.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 过滤器
 */
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Resource
    private JwtProperties prop;

    @Resource
    private FilterProperties filterProperties;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    /**
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        /**
         * 获取请求路径
         */
        StringBuffer requestURL = request.getRequestURL();
        /**
         * 判断当前请求路径是否在白名单
         */
        for (String url : this.filterProperties.getAllowPaths()) {
            if (StringUtils.contains(requestURL.toString(), url)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        /**
         * 获取zuul上下文
         */
        RequestContext context = RequestContext.getCurrentContext();
        try {
            /**
             * 获取请求对象
             */
            HttpServletRequest request = context.getRequest();
            /**
             * 获取token信息
             */
            String token = CookieUtils.getCookieValue(request, this.prop.getCookieName());

            /**
             * 解析token信息
             */
            JwtUtils.getInfoFromToken(token, this.prop.getPublicKey());
        } catch (Exception e) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            e.printStackTrace();
        }
        return null;
    }
}
