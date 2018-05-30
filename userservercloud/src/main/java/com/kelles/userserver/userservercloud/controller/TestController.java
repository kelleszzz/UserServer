package com.kelles.userserver.userservercloud.controller;

import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.fileserver.fileserversdk.sdk.FileServerSDKTest;
import com.kelles.userserver.userservercloud.service.UserDatabaseService;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.userserversdk.setting.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Deprecated
@Controller
@RequestMapping("/test")
public class TestController extends BaseController {
    @Autowired
    FileServerSDKTest fileServerSDKTest;

    @Autowired
    UserDatabaseService userDatabaseService;

    final static String testId = "kelleszzz";
    final static String testAccessCode = "tom44123";
    final static Random random = new Random(System.currentTimeMillis());

    @RequestMapping("/testfileserver")
    @ResponseBody
    public String testFileServer() {
        StringBuilder sb = new StringBuilder();
        try {
            try {
                fileServerSDKTest.test1_Insert();
                fileServerSDKTest.test2_Get();
                fileServerSDKTest.test3_Get_No_Content();
                fileServerSDKTest.test4_Update();
            }  finally {
                fileServerSDKTest.test5_Remove();
            }
            return "Congratulations!";
        } catch (Error e) {
            e.printStackTrace();
            return "Error, File Server not Running";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
    }

    @RequestMapping("testinsert")
    @ResponseBody
    public String testInsert() {
        Connection conn = null;
        UserDTO userDTO = null;
        try {
            conn = userDatabaseService.getConnection();
            userDTO = new UserDTO();
            userDTO.setId(testId);
            userDTO.setAccess_code(testAccessCode);
            List<FileDTO> fileDTOList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FileDTO fileDTO = new FileDTO();
                String id = String.valueOf(random.nextInt(10000));
                fileDTO.setId(id);
                fileDTO.setAccess_code(testAccessCode);
                fileDTO.setFile_name("file" + id);
                fileDTOList.add(fileDTO);
            }
            userDTO.setContent(fileDTOList);
            int rowsAffected = userDatabaseService.insertUserDTO(userDTO, conn);
            return gson.toJson(Util.getResultDO(true, rowsAffected));
        } catch (Exception e) {
            logger.error("Test Insert, userDTO = {}", gson.toJson(Util.userDTOInfo(userDTO)));
            e.printStackTrace();
            return gson.toJson(Util.getResultDO(false, Setting.STATUS_ERROR));
        } finally {
            closeConnection(conn);
        }
    }
}
