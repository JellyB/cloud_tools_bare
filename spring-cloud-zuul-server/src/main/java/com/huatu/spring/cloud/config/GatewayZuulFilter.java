package com.huatu.spring.cloud.config;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.CommonResult;
import com.huatu.tiku.springboot.users.support.SessionRedisTemplate;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @author zhouwei
 * @Description: 网关认证过滤器
 * @create 2018-05-31 下午1:47
 **/
@Slf4j
@Component
public class GatewayZuulFilter extends ZuulFilter {

    private static List<String> whiteUrls;

    static {
        whiteUrls = Lists.newArrayList();
        // 获取验证码 登陆
        whiteUrls.add("/user/v1/user/phoneCode");
        whiteUrls.add("/user/v1/user/phoneLogin");
        whiteUrls.add("/user/v1/user/login");
        whiteUrls.add("/user/v1/user/thirdLogin");
        whiteUrls.add("/user/v1/user/info/[0-9]+");
        whiteUrls.add("/user/v1/user/register");
        whiteUrls.add("/user/v1/user/resetPassword");
        whiteUrls.add("/user/v1/user/search");
        whiteUrls.add("/user/v1/user/check");
        whiteUrls.add("/user/v1/appVersion/check");
        whiteUrls.add("/user/v1/attention/[0-9]+/fans");
        whiteUrls.add("/user/v1/dic/UserType");
        whiteUrls.add("/u/v2/channel/cool");
        whiteUrls.add("/user/v2/user/thirdLogin");
        whiteUrls.add("/user/v1/user/thirdReg");
        // 题库相关
        whiteUrls.add("/tk/v1/question/record");
        whiteUrls.add("/tk/v1/video/list");
        whiteUrls.add("/tk/v1/video/answer");
        whiteUrls.add("/tk/v1/advert/[0-9]+");
        whiteUrls.add("/tk/v1/question/newest");
        whiteUrls.add("/tk/v1/question/nationArea/[0-9]+");
        whiteUrls.add("/tk/v1/question/detail/[0-9]+");
        whiteUrls.add("/tk/v1/question/details/[0-9]+");
        whiteUrls.add("/tk/v1/question/type/[0-9]+");
        whiteUrls.add("/tk/v1/question/type/root");
        whiteUrls.add("/tk/v2/question/type/root");
        whiteUrls.add("/tk/v1/question/type/noLeader");
        whiteUrls.add("/tk/v1/exam/list");
        whiteUrls.add("/tk/v1/exam/rule");
        whiteUrls.add("/tk/v1/position/[0-9]+");
        whiteUrls.add("/tk/v1/dic/[a-zA-Z]+");
        whiteUrls.add("/tk/v2/dic/[a-zA-Z]+");
        whiteUrls.add("/tk/v1/organization/[0-9]+/child");
        whiteUrls.add("/tk/v1/position/option/label/");
        whiteUrls.add("/tk/v1/position/[0-9]+/exercise");
        whiteUrls.add("/tk/v1/position/course");
        whiteUrls.add("/tk/v1/nationArea/[A-Z_]+");
        whiteUrls.add("/tk/v1/organization/type/[A-Z_]+");
        whiteUrls.add("/tk/v1/position/[0-9]+/tag/[A-Z_]+");
        whiteUrls.add("/tk/v1/position/option/label");
        whiteUrls.add("/tk/v1/reference/[0-9]+/[A-Z_]+");
        whiteUrls.add("/tk/v1/question/type/[0-9]+/children");
        whiteUrls.add("/tk/v1/hotWord/type/[0-9]+");
        whiteUrls.add("/tk/v1/image");
        whiteUrls.add("/tk/v1/question/search");
        whiteUrls.add("/tk/v1/question/validate/[0-9]+");
        whiteUrls.add("/tk/v1/report/detail/practice/[0-9]+");
        whiteUrls.add("/tk/v1/report/detail/exam/[0-9]+");
        whiteUrls.add("/tk/v1/playRecord/report");
        whiteUrls.add("/tk/v1/comment/[0-9]+/list");
        whiteUrls.add("/user/v1/attention/user/[0-9]+");
        whiteUrls.add("/tk/v1/video/shareCount");
        whiteUrls.add("/tk/v1/video/detail/[0-9]+");
        whiteUrls.add("/tk/v1/comment/ms");
        whiteUrls.add("/tk/v1/question/audio/[0-9]+");
        whiteUrls.add("/tk/interview/[0-9a-zA-Z/]+");
        whiteUrls.add("/tk/v1/TProduct");
        whiteUrls.add("/tk/v1/TProduct/wxPayCallback");
        whiteUrls.add("/tk/v1/TProduct/aliPayCallback");
        whiteUrls.add("/tk/v1/commentCommodity");
        whiteUrls.add("/tk/v1/rechargeInstruction/info");
        whiteUrls.add("/tk/v1/examCommodity/[A-Z_]+");
        whiteUrls.add("/tk/v1/commodityOrder/aliPayCallback");
        whiteUrls.add("/tk/v1/commodityOrder/wxPayCallback");
        whiteUrls.add("/tk/v1/referenceMaterial/modules");
        whiteUrls.add("/tk/v1/referenceMaterial/moduleInfo");
        whiteUrls.add("/tk/v1/referenceMaterial/categories");
        whiteUrls.add("/tk/v1/referenceMaterial");
        whiteUrls.add("/tk/v1/referenceMaterial/[0-9]+");
        whiteUrls.add("/tk/v1/questionTopic/list");
        whiteUrls.add("/tk/v1/questionTopic/[0-9]+");
        whiteUrls.add("/tk/v1/questionTopic/question");
        whiteUrls.add("/tk/v1/person/dynamic");
        whiteUrls.add("/tk/v1/referenceMaterial/browseRecord/[0-9]+");
        // 搜索相关
        whiteUrls.add("/s/v1/user/search");
        whiteUrls.add("/s/v1/question/search");
        whiteUrls.add("/s/v1/hotWord/type/[A-Z_]+");
        // 课程相关
        whiteUrls.add("/c/v1/ic/courses/icClassList");
        whiteUrls.add("/c/v1/ic/courses/[0-9]+");
        whiteUrls.add("/c/v1/ic/courses/[0-9]+/getClassExt");
        whiteUrls.add("/c/v1/ic/courses/[0-9]+");
        whiteUrls.add("/co/v1/comments/netClassId/[0-9]+");
        whiteUrls.add("/co/v1/comments/classId/[0-9]+/coursewareId/[0-9]+");
        whiteUrls.add("/co/api/v1/aliPay/sync");
        whiteUrls.add("/co/api/v1/weChatPayBack/sync/[0-9a-zA-Z]+");
        whiteUrls.add("/co/v4/courses/teacher/[0-9]+");
        whiteUrls.add("/co/v4/courses/teachers/[0-9]+");
        whiteUrls.add("/co/v4/courses/classBeforeSyllabuses");

        //资料评论
        whiteUrls.add("/tk/v1/material/comment/list");
        whiteUrls.add("/tk/v1/material/comment/detail");
        whiteUrls.add("/tk/v1/material/comment/[0-9]+");

        // 分享相关
        whiteUrls.add("/share/.+");
        //活动上报
        whiteUrls.add("/tk/v1/activity/reportCount");
        //广告上报
        whiteUrls.add("/tk/v1/advert/reportCount");
        //机器打分获取规则自测
        whiteUrls.add("/tk/v1/question/systemScoreRule/[0-9]+");
        // 钉钉小程序接口
        whiteUrls.add("/dtmp/[0-9a-zA-Z/]+");
        // 后台添加评论
        whiteUrls.add("/tk/v1/comment/ms");
        // 百家云视频回调
        whiteUrls.add("/tk/v1/video/transfer");
        //神策埋点
        whiteUrls.add("/tk/v1/sensors/question/info/[0-9]+");
        whiteUrls.add("/tk/v1/sensors/examRoom/info/[0-9]+");
    }

    @Autowired
    private SessionRedisTemplate sessionRedisTemplate;

    // token校验逻辑V2，将状态码统一改为200
    private final String V2 = "1.0.1";

    /**
     * per：路由之前
     * routing：路由时
     * post：路由后
     * error：错误时调用
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器顺序，类似@Filter中的order
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String url = request.getRequestURI();
        String token = request.getHeader("token");

        boolean whiteFlag = valueUrl(url);
        if (token == null) {
            // 未传token & 白名单
            if (whiteFlag) {
                return null;
            }
            log.info("-------GatewayZuulFilter---token为空");
            ctx.setSendZuulResponse(false);

            String cv = request.getHeader("cv");
            if (V2.compareTo(cv) > 0) {
                ctx.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
            } else {
                ctx.setResponseStatusCode(HttpStatus.OK.value());
            }
            JSONObject r = new JSONObject();
            r.put("code", CommonResult.PERMISSION_DENIED.getCode());
            r.put("message", CommonResult.PERMISSION_DENIED.getMessage());
            ctx.setResponseBody(r.toJSONString());
            return null;
        } else {
            log.info("-------GatewayZuulFilter---token值:" + token);
            Long id = Long.parseLong(Optional.ofNullable(sessionRedisTemplate.hget(token, "id")).orElse("0").toString());
            log.info("-------GatewayZuulFilter---用户id值:" + id);
            if (id == 0) {
                // 传入无效token & 白名单
                if (whiteFlag) {
                    return null;
                }
                ctx.setSendZuulResponse(false);

                String cv = request.getHeader("cv");
                if(StringUtils.isNotBlank(cv)){
                    if (V2.compareTo(cv) > 0) {
                        ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                    } else {
                        ctx.setResponseStatusCode(HttpStatus.OK.value());
                    }
                } else {
                    ctx.setResponseStatusCode(HttpStatus.OK.value());
                }

                JSONObject r = new JSONObject();
                r.put("code", CommonResult.LOGIN_ON_OTHER_DEVICE_RECOMMENDED_CHANGE_PASSWD.getCode());
                r.put("message", CommonResult.LOGIN_ON_OTHER_DEVICE_RECOMMENDED_CHANGE_PASSWD.getMessage());
                ctx.setResponseBody(r.toJSONString());
                return null;
            }
            String cv = request.getHeader("cv");
            String terminal = request.getHeader("terminal");
            log.info("{}$$${}$$${}", request.getRequestURI(), cv, terminal);
            ctx.getRequest().getParameterMap();
            Map<String, List<String>> requestParams = ctx.getRequestQueryParams();
            if (requestParams == null) {
                requestParams = Maps.newHashMap();
                requestParams.put("loginUserId", Arrays.asList(id + ""));
                ctx.setRequestQueryParams(requestParams);
            } else {
                requestParams.put("loginUserId", Arrays.asList(id + ""));
            }
        }
        return null;
    }

    /**
     * 检查路由是否在白名单
     *
     * @param url 路由
     * @return true/false
     */
    private boolean valueUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        for (String routeReg : whiteUrls) {
            if (url.matches(routeReg)) {
                return true;
            }
        }

        return false;
    }

}