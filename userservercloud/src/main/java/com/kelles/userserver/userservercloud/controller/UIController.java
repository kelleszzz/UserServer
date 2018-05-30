package com.kelles.userserver.userservercloud.controller;

import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.fileserver.fileserversdk.data.ResultDO;
import com.kelles.fileserver.fileserversdk.sdk.FileServerSDK;
import com.kelles.userserver.userservercloud.service.UserDatabaseService;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import com.kelles.userserver.userservercloud.userserversdk.sdk.UserServerSDK;
import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.userserversdk.setting.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping(Setting.PATH_UI)
public class UIController extends BaseController {

    @Autowired
    FileServerSDK fileServerSDK;

    @Autowired
    UserDatabaseService userDatabaseService;

    @RequestMapping(Setting.PATH_INDEX)
    public String index(Model model) {
        model.addAttribute("grantUrl", Setting.PATH_UI + Setting.PATH_GRANT);
        model.addAttribute("regrantUrl", Setting.PATH_UI + Setting.PATH_REGRANT);
        model.addAttribute("getUrl", Setting.PATH_UI + Setting.PATH_GET);
        return "uploadForm";
    }

    @RequestMapping(Setting.PATH_GET)
    public Object get(@RequestParam String id,
                      @RequestParam String access_code,
                      Model model) {
        Connection conn = null;
        UserDTO userDTO = null;
        try {
            conn = userDatabaseService.getConnection();
            //获取UserDTO并作安全检查
            userDTO = userDatabaseService.getUserDTO(id, true, conn);
            if (userDTO == null) {
                return gson.toJson(Util.getResultDO(true, Setting.STATUS_USER_NOT_FOUND, Setting.MESSAGE_USER_NOT_FOUND));
            }
            if (!securityCheck(id, access_code, userDTO)) {
                return gson.toJson(Util.getResultDO(false, Setting.STATUS_ACCESS_DENIED, Setting.MESSAGE_ACCESS_DENIED));
            }
        } finally {
            closeConnection(conn);
        }
        //获取User下的全部授权文件
        List<FileDTO> fileDTOS = new ArrayList<>();
        for (Iterator<FileDTO> iterator = userDTO.getContent().iterator(); iterator.hasNext(); ) {
            FileDTO fileDTO = iterator.next();
            if (Util.isEmpty(fileDTO.getId()) || Util.isEmpty(fileDTO.getAccess_code())) continue;
            ResultDO<FileDTO> resultDO = fileServerSDK.get(fileDTO.getId(), fileDTO.getAccess_code(), false);
            if (!resultDO.getSuccess() || resultDO.getData() == null) continue;
            FileDTO accessFileDTO = resultDO.getData();
            fileDTOS.add(accessFileDTO);
        }
        //添加至Model
        model.addAttribute("fileServerSDK", fileServerSDK);
        model.addAttribute("fileDTOS", fileDTOS);
        logger.info("UIController#Get, UserDTO = {}, fileDTOs = {}", gson.toJson(userDTO), gson.toJson(fileDTOS));
        return "displayGet";
    }

    @RequestMapping(Setting.PATH_GRANT)
    @ResponseBody
    public Object grant(@RequestParam String id,
                        @RequestParam String access_code,
                        @RequestParam String file_id,
                        @RequestParam String file_access_code,
                        Model model) {
        model.addAttribute("id", id);
        model.addAttribute("access_code", access_code);
        List<FileDTO> fileDTOS = new ArrayList<>();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(file_id);
        fileDTO.setAccess_code(file_access_code);
        fileDTOS.add(fileDTO);
        model.addAttribute("files", gson.toJson(fileDTOS));
        return new ModelAndView("redirect:" + Setting.URL_GRANT, model.asMap());
    }

    @RequestMapping(Setting.PATH_REGRANT)
    @ResponseBody
    public Object regrant(@RequestParam String id,
                          @RequestParam String access_code,
                          @RequestParam String file_id,
                          Model model) {
        model.addAttribute("id", id);
        model.addAttribute("access_code", access_code);
        List<FileDTO> fileDTOS = new ArrayList<>();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(file_id);
        fileDTOS.add(fileDTO);
        model.addAttribute("files", gson.toJson(fileDTOS));
        return new ModelAndView("redirect:" + Setting.URL_REGRANT, model.asMap());
    }
}
