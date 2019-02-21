package com.github.steven.config;

import com.github.steven.bean.WeChatConfig;
import lombok.Data;

/**
 * AutoConfig.
 *
 * @author steven
 */
@Data
public class AutoConfig {

    private WeChatConfig weChatConfig;

    private String imageDir;

    private String keyword;
}
