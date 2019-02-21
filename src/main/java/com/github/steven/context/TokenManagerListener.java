package com.github.steven.context;

import com.github.steven.bean.WeChatConfig;
import com.github.steven.config.AutoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import weixin.popular.support.TokenManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * TokenManagerListener.
 *
 * @author steven
 */
public class TokenManagerListener implements ServletContextListener {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenManagerListener.class);

    @Autowired
    private AutoConfig autoConfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //WEB容器 初始化时调用
        TokenManager.setDaemon(false);
        WeChatConfig weChatConfig = autoConfig.getWeChatConfig();
        String appId = weChatConfig.getAppId();
        String appSercret = weChatConfig.getAppSercret();
        LOGGER.debug("appId:" + appId + ", appSercret:" + appSercret);
        TokenManager.init(appId, appSercret);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //WEB容器  关闭时调用
        TokenManager.destroyed();
    }
}
