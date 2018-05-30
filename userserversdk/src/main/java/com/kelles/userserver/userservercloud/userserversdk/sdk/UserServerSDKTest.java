package com.kelles.userserver.userservercloud.userserversdk.sdk;

import com.google.gson.Gson;
import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.fileserver.fileserversdk.data.ResultDO;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.userserversdk.setting.Util;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserServerSDKTest {
    Gson gson = new Gson();
    UserServerSDK userServerSDK = new UserServerSDK();
    Random random = new Random(System.currentTimeMillis());
    String testId = "kelleszzz";
    String testAccessCode = "tom44123";

    public static void main(String[] args) throws IOException {
        UserServerSDKTest test = new UserServerSDKTest();
        try {
            test.test1_Insert();
            test.test2_Grant();
            test.test3_Regrant();
            test.test4_Get();
        } finally {
            test.test5_Remove();
        }
    }

    @Test
    public void test1_Insert() throws IOException {
        Util.log("=======test1_Insert=======");
        ResultDO resultDO = userServerSDK.insert(testId,testAccessCode);
        Assert.assertTrue(resultDO.getSuccess() || resultDO.getCode() == Setting.STATUS_FILE_ALREADY_EXISTS);
    }

    @Test
    public void test2_Grant() {
        Util.log("=======test2_Grant=======");
        List<FileDTO> fileDTOS = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setId(String.valueOf(random.nextInt(10000)));
            fileDTO.setAccess_code("tom44123");
            fileDTOS.add(fileDTO);
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(testId);
        fileDTO.setAccess_code(testAccessCode);
        fileDTOS.add(fileDTO);
        ResultDO resultDO = userServerSDK.grant(testId, testAccessCode, fileDTOS);
        Assert.assertTrue(resultDO.getSuccess());
    }

    @Test
    public void test3_Regrant() throws IOException {
        Util.log("=======test3_Regrant=======");
        List<FileDTO> fileDTOS = new ArrayList<>();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(testId);
        fileDTO.setAccess_code(testAccessCode);
        fileDTOS.add(fileDTO);
        ResultDO resultDO = userServerSDK.regrant(testId, testAccessCode, fileDTOS);
        Assert.assertTrue(resultDO.getSuccess());
    }

    @Test
    public void test4_Get() throws IOException {
        Util.log("=======test4_Get=======");
        ResultDO<UserDTO> resultDO = userServerSDK.get(testId, testAccessCode);
        Assert.assertTrue(resultDO.getSuccess() && resultDO.getData() != null && resultDO.getData().getContent() != null);
        UserDTO userDTO = resultDO.getData();
        Assert.assertTrue(userDTO.getContent().size() == 3);
    }

    @Test
    public void test5_Remove() throws IOException {
        Util.log("=======test5_Remove=======");
        ResultDO resultDO = userServerSDK.remove(testId, testAccessCode);
        Assert.assertTrue(resultDO.getSuccess());
    }

}
