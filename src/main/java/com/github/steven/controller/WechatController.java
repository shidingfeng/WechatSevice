package com.github.steven.controller;

import com.github.steven.bean.WeChatConfig;
import com.github.steven.config.AutoConfig;
import com.github.steven.context.TokenManagerListener;
import com.github.steven.service.WechatService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import weixin.popular.api.MessageAPI;
import weixin.popular.api.UserAPI;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.support.ExpireKey;
import weixin.popular.support.TokenManager;
import weixin.popular.support.expirekey.DefaultExpireKey;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * WechatController.
 *
 * @author steven
 */
@RestController
public class WechatController {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatController.class);

    //重复通知过滤
    private static ExpireKey expireKey = new DefaultExpireKey();

    @Autowired
    private AutoConfig autoConfig;

    @Autowired
    private WechatService wechatService;

    @RequestMapping(value = "/wechat")
    public String getAuth(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        LOGGER.debug("signature:" + signature + "\n" +
                "timestamp:" + timestamp + "\n" +
                "nonce:" + nonce + "\n" +
                "echostr:" + echostr + "\n");

        if (echostr != null) {
            return echostr;
        }

        String token = autoConfig.getWeChatConfig().getToken();
        //验证请求签名
        if (!signature.equals(SignatureUtil.generateEventMessageSignature(token, timestamp, nonce))) {
            LOGGER.error("The request signature is invalid");
            return "fail";
        }
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            LOGGER.error("getInputStream fail", e);
        }

        if (inputStream != null) {
            //转换XML
            EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, inputStream);
            String key = eventMessage.getFromUserName() + "__"
                    + eventMessage.getToUserName() + "__"
                    + eventMessage.getMsgId() + "__"
                    + eventMessage.getCreateTime();
            if (expireKey.exists(key)) {
                //重复通知不作处理
                return "";
            } else {
                expireKey.add(key);
            }

            String keyWord = autoConfig.getKeyword();
            if (keyWord.equals(eventMessage.getContent())) {
                //创建回复
                String fromUserName = eventMessage.getFromUserName();
                String toUserName = eventMessage.getToUserName();
                String imageXmlMessage = wechatService.sendImage(fromUserName, toUserName);
                //回复
                return imageXmlMessage;
            }
        }

        return "";

    }
}
