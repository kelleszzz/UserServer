package com.kelles.userserver.userservercloud.configuration;

import com.google.gson.Gson;
import com.kelles.fileserver.fileserversdk.sdk.FileServerSDK;
import com.kelles.fileserver.fileserversdk.sdk.FileServerSDKTest;
import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.userserversdk.setting.Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Configuration
public class BaseConfiguration {
    @Bean
    public FileServerSDK fileServerSDK(){
        return new FileServerSDK();
    }

    @Bean
    public FileServerSDKTest fileServerSDKTest(){
        return new FileServerSDKTest();
    }

    @Bean(value = "gson")
    public Gson gson(){
        return new Gson();
    }

    @Bean(value = "defaultCharset")
    public Charset defaultCharset(){
        return Setting.DEFAULT_CHARSET;
    }

    @Bean(value = "util")
    public Util util(){
        return new Util();
    }
}
