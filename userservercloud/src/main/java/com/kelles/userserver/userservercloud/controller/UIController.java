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
    UserServerSDK userServerSDK;

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
                      @RequestParam(required = false) Integer count,
                      Model model) {
        ResultDO<UserDTO> resultDO = userServerSDK.get(id, access_code, count == null ? Integer.MAX_VALUE : count.intValue());
        if (resultDO.getSuccess()) {
            List<FileDTO> fileDTOS = resultDO.getData().getContent();
            //添加至Model
            model.addAttribute("fileServerSDK", fileServerSDK);
            model.addAttribute("fileDTOS", fileDTOS);
            logger.info("UIController#Get, UserDTO = {}, fileDTOs = {}", gson.toJson(resultDO.getData()), gson.toJson(fileDTOS));
        }
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
