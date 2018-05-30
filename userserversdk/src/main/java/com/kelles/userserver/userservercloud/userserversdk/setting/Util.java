package com.kelles.userserver.userservercloud.userserversdk.setting;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import org.apache.commons.beanutils.BeanUtils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

public class Util extends com.kelles.fileserver.fileserversdk.setting.Util {
    public static UserDTO userDTOInfo(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        try {
            UserDTO userDTOInfo = new UserDTO();
            BeanUtils.copyProperties(userDTOInfo, userDTO);
            userDTOInfo.setInputStream(null);
            return userDTOInfo;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将UserDTO中的InputStream转换为Content
     * 不会关闭InputStream
     *
     * @param gson
     * @return
     */
    public static boolean convertInputStreamIntoContent(UserDTO userDTO, Gson gson) {
        if (userDTO == null || userDTO.getInputStream() == null || gson == null) return false;
        byte[] bytes = inputStreamToBytes(userDTO.getInputStream());
        if (bytes == null) return false;
        String json = new String(bytes, Setting.DEFAULT_CHARSET);
        try {
            List<FileDTO> content = gson.fromJson(json, Setting.TYPE_LIST_FILEDTO);
            userDTO.setContent(content);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * 将UserDTO中的Content转换为InputStream
     *
     * @param userDTO
     * @return
     */
    public static boolean convertContentIntoInputStream(UserDTO userDTO, Gson gson) {
        if (userDTO == null || isEmpty(userDTO.getContent()) || gson == null) return false;
        String json = gson.toJson(userDTO.getContent());
        InputStream inputStream = bytesToInputStream(json.getBytes(Setting.DEFAULT_CHARSET));
        if (inputStream == null) return false;
        userDTO.setInputStream(inputStream);
        return true;
    }

}
