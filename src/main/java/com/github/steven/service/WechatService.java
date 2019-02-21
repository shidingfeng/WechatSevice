package com.github.steven.service;

import com.github.steven.config.AutoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weixin.popular.api.MediaAPI;
import weixin.popular.bean.media.Media;
import weixin.popular.bean.media.MediaType;
import weixin.popular.bean.xmlmessage.XMLImageMessage;
import weixin.popular.support.TokenManager;

import java.io.File;

/**
 * WechatService.
 *
 * @author steven
 */
@Service
public class WechatService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatService.class);

    @Autowired
    private AutoConfig autoConfig;

    /**
     * 上传素材后 拼接imageMessage 返回
     * @param fromUserName 开发者微信号
     * @param toUserName 接收方帐号（收到的OpenID）
     * @param file 图片资源
     * @return xmlImageMessage
     */
    public String sendImage(String toUserName, String fromUserName, File file) {
        String accessToken = TokenManager.getDefaultToken();
        //上传临时素材
        Media media = MediaAPI.mediaUpload(accessToken, MediaType.image, file);
        String mediaId = media.getMedia_id();
        LOGGER.debug("mediaId:" + mediaId);

        //发送图片
        XMLImageMessage xmlImageMessage = new XMLImageMessage(toUserName, fromUserName, mediaId);
        return xmlImageMessage.toXML();
    }

    /**
     * 发送随机图片
     * @param fromUserName 开发者微信号
     * @param toUserName 接收方帐号（收到的OpenID）
     * @return xmlImageMessage
     */
    public String sendImage(String toUserName, String fromUserName) {
        File image = getImage();
        LOGGER.info(image.getName());
        return this.sendImage(toUserName, fromUserName, image);
    }

    /**
     * 获取图片文件
     * @return imageFile 图片文件
     */
    public File getImage() {
        String imageDir = autoConfig.getImageDir();
        File file = new File(imageDir);
        File imageFile = getRandomImageFile(file);
        return imageFile;
    }

    /**
     * 递归随机获取一个imageFile
     * @param file file 目录继续查询，文件返回
     * @return imageFile
     */
    private File getRandomImageFile(File file) {
        File[] files = file.listFiles();
        int randomFileNumber = (int)(Math.random() * (files.length-1));
        File randomFile = files[randomFileNumber];
        if (randomFile.isDirectory()) {
            return getRandomImageFile(randomFile);
        }
        return randomFile;
    }
}
