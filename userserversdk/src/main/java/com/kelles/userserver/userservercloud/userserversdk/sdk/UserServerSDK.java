package com.kelles.userserver.userservercloud.userserversdk.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.fileserver.fileserversdk.data.ResultDO;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.userserversdk.setting.Util;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class UserServerSDK {

    protected OkHttpClient client = new OkHttpClient();
    protected Gson gson = new Gson();

    public ResultDO remove(String id, String access_code) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code)) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        Response response = null;
        ResultDO resultDO = null;
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("access_code", access_code)
                    .build();
            Request request = new Request.Builder()
                    .url(Setting.URL_BASIC + Setting.URL_REMOVE)
                    .post(requestBody)
                    .build();
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response);
            if (resultDO.getSuccess()) {
                Util.log("Remove userDTO = %s, \nresult = %s", "{id=" + id + ",access_code=" + access_code + "}", gson.toJson(Util.resultDOInfo(resultDO)));
            } else {
                Util.log("Remove Error, userDTO = %s, response_code = %s\nresult = %s", "{id=" + id + ",access_code=" + access_code + "}",
                        response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            }
            return resultDO;
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Remove Error, userDTO = %s, \nresult = %s", "{id=" + id + ",access_code=" + access_code + "}", gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    public ResultDO grant(String id, String access_code, List<FileDTO> fileDTOS){
        return grant(id,access_code,fileDTOS,false);
    }

    public ResultDO regrant(String id, String access_code, List<FileDTO> fileDTOS){
        return grant(id,access_code,fileDTOS,true);
    }

    protected ResultDO grant(String id, String access_code, List<FileDTO> fileDTOS, boolean regrant) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code) || Util.isEmpty(fileDTOS) || fileDTOS.size() == 0) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        //清除无效内容
        if (regrant){
            fileDTOS.removeIf(fileDTO -> Util.isEmpty(fileDTO.getId()));
        } else {
            fileDTOS.removeIf(fileDTO -> Util.isEmpty(fileDTO.getId()) || Util.isEmpty(fileDTO.getAccess_code()));
        }
        Response response = null;
        ResultDO resultDO = null;
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("access_code", access_code)
                    .addFormDataPart("files", gson.toJson(fileDTOS))
                    .build();
            Request request = null;
            if (regrant) {
                //regrant
                request = new Request.Builder()
                        .url(Setting.URL_BASIC + Setting.URL_REGRANT)
                        .post(requestBody)
                        .build();
            } else {
                //grant
                request = new Request.Builder()
                        .url(Setting.URL_BASIC + Setting.URL_GRANT)
                        .post(requestBody)
                        .build();
            }
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response);
            if (resultDO.getSuccess()) {
                Util.log((regrant ? "Regrant" : "Grant") + " userDTO = %s, files = %s\nresult = %s", "[id=" + id + ", access_code=" + access_code + "]", gson.toJson(fileDTOS),
                        gson.toJson(Util.resultDOInfo(resultDO)));
            } else {
                Util.log((regrant ? "Regrant" : "Grant") + " Error,  userDTO = %s, files = %s\nresult = %s", "[id=" + id + ", access_code=" + access_code + "]", gson.toJson(fileDTOS),
                        gson.toJson(Util.resultDOInfo(resultDO)));
            }
            return resultDO;
        } catch (IOException e) {
            e.printStackTrace();
            Util.log((regrant ? "Regrant" : "Grant") + " Error, userDTO = %s, files = %s\nresult = %s", "[id=" + id + ", access_code=" + access_code + "]", gson.toJson(fileDTOS),
                    resultDO == null ? null : gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    public ResultDO insert(String id, String access_code) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code)) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setAccess_code(access_code);
        Response response = null;
        ResultDO resultDO = null;
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", userDTO.getId())
                    .addFormDataPart("access_code", userDTO.getAccess_code())
                    .build();
            Request request = new Request.Builder()
                    .url(Setting.URL_BASIC + Setting.URL_INSERT)
                    .post(requestBody)
                    .build();
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response);
            if (resultDO.getSuccess()) {
                Util.log("Insert userDTO = %s, \nresult = %s", gson.toJson(Util.userDTOInfo(userDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
            } else {
                Util.log("Insert Error, userDTO = %s, response_code = %s\nresult = %s", gson.toJson(Util.userDTOInfo(userDTO)),
                        response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            }
            return resultDO;
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Insert Error, userDTO = %s, \nresult = %s", gson.toJson(Util.userDTOInfo(userDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    /**
     * @param id
     * @param access_code
     * @return
     */
    public ResultDO<UserDTO> get(String id, String access_code, boolean getContent) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code)) {
            return Util.<UserDTO>getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        UserDTO userDTO = null;
        ResultDO<UserDTO> resultDO = null;
        Response response = null;
        try {
            HttpUrl httpUrl = HttpUrl.parse(Setting.URL_BASIC + Setting.URL_GET).newBuilder()
                    .addQueryParameter("id", id)
                    .addQueryParameter("access_code", access_code)
                    .addQueryParameter("getContent", String.valueOf(getContent))
                    .build();
            Request request = new Request.Builder().url(httpUrl).build();
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response, new TypeToken<ResultDO<UserDTO>>() {
            }.getType());
            userDTO = resultDO.getData();
            if (resultDO.getSuccess()) {
                Util.log("Get userDTO = %s, \nresult = %s", gson.toJson(Util.userDTOInfo(userDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
            } else {
                Util.log("Get Error, id = %s, access_code = %s, response_code = %s\nresult = %s", id, access_code,
                        response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            }
            return resultDO;
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Get Error, id = %s, access_code = %s, \nresult = %s", id, access_code, gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    public ResultDO<UserDTO> get(String id, String access_code) {
        return get(id, access_code, true);
    }

    /**
     * 从ResponseBody中提取出json,转换为ResultDO
     *
     * @param response
     * @return 始终不为null
     */
    protected <T> ResultDO<T> responseToResultDO(Response response, Type typeResultT) {
        ResultDO<T> resultDO = null;
        try {
            if (typeResultT != null && response != null && response.isSuccessful() && response.body() != null) {
                String responseBody = new String(response.body().bytes(), Setting.DEFAULT_CHARSET);
                try {
                    resultDO = gson.fromJson(responseBody, typeResultT);
                } catch (JsonSyntaxException e) {
                    Util.log("ResponseToResultDO Error, json = %s", responseBody);
                    resultDO = Util.getResultDO(false, Setting.STATUS_PARSE_JSON_ERROR, Setting.MESSAGE_PARSE_JSON_ERROR);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (resultDO == null) {
            Util.log("ResponseToResultDO Error, code = %s", response == null ? "null" : String.valueOf(response.code()));
            resultDO = Util.getResultDO(false, Setting.STATUS_RESPONSE_FAILURE, Setting.MESSAGE_RESPONSE_FAILURE);
        }
        if (resultDO == null) throw new NullPointerException("responseToResultDO is NULL!");
        return resultDO;
    }

    protected <T> ResultDO<T> responseToResultDO(Response response) {
        return responseToResultDO(response, new TypeToken<ResultDO<T>>() {
        }.getType());
    }
}
