package com.kelles.userserver.userservercloud.controller;

import com.google.gson.JsonSyntaxException;
import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.fileserver.fileserversdk.data.ResultDO;
import com.kelles.userserver.userservercloud.service.UserDatabaseService;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.userserversdk.setting.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.*;

@Controller
@RequestMapping(Setting.PATH_USER)
public class UserController extends BaseController {
    @Autowired
    UserDatabaseService userDatabaseService;

    @RequestMapping(Setting.PATH_INSERT)
    @ResponseBody
    public String insert(@RequestParam String id,
                         @RequestParam String access_code) {
        Connection conn = null;
        UserDTO userDTO = null;
        try {
            conn = userDatabaseService.getConnection();
            userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setAccess_code(access_code);
            int rowsAffected = userDatabaseService.insertUserDTO(userDTO, conn);
            return gson.toJson(Util.getResultDO(rowsAffected > 0, rowsAffected));
        } catch (Exception e) {
            logger.error("Test Insert, userDTO = {}", gson.toJson(Util.userDTOInfo(userDTO)));
            e.printStackTrace();
            return gson.toJson(Util.getResultDO(false, Setting.STATUS_ERROR));
        } finally {
            closeConnection(conn);
        }
    }

    @RequestMapping(Setting.PATH_GET)
    @ResponseBody
    public String get(@RequestParam String id,
                      @RequestParam String access_code,
                      @RequestParam(required = false) Boolean getContent,
                      @RequestParam(required = false) Integer count) {
        Connection conn = null;
        UserDTO userDTO = null;
        try {
            conn = userDatabaseService.getConnection();
            //获取UserDTO并作安全检查
            userDTO = userDatabaseService.getUserDTO(id, !Boolean.FALSE.equals(getContent), conn);
            if (userDTO == null) {
                return gson.toJson(Util.getResultDO(true, Setting.STATUS_USER_NOT_FOUND, Setting.MESSAGE_USER_NOT_FOUND));
            }
            if (!securityCheck(id, access_code, userDTO)) {
                return gson.toJson(Util.getResultDO(false, Setting.STATUS_ACCESS_DENIED, Setting.MESSAGE_ACCESS_DENIED));
            }
            //get
            ResultDO<UserDTO> resultDO = Util.getResultDO(true);
            resultDO.setData(userDTO);
            //根据CreateTime排序
            List<FileDTO> fileDTOS = userDTO.getContent();
            Collections.sort(fileDTOS, new Comparator<FileDTO>() {
                @Override
                public int compare(FileDTO o1, FileDTO o2) {
                    if (o1.getCreate_time() == null || o2.getCreate_time() == null) return 0;
                    return o1.getCreate_time().intValue() > o2.getCreate_time().intValue() ? 1 : -1;
                }
            });
            if (count != null) {
                List<FileDTO> filteredFileDTOS = new ArrayList<>();
                for (int i = 0; i < count.intValue(); i++) {
                    filteredFileDTOS.add(fileDTOS.get(i));
                }
                userDTO.setContent(filteredFileDTOS);
            }
            return gson.toJson(resultDO);
        } catch (Exception e) {
            logger.error("Test Insert, userDTO = {}", gson.toJson(Util.userDTOInfo(userDTO)));
            e.printStackTrace();
            return gson.toJson(Util.getResultDO(false, Setting.STATUS_ERROR));
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 将files转换为List<FileDTO>,并以id为key添加至Content中;重复id会被更新
     *
     * @param id
     * @param access_code
     * @param files
     * @return
     */
    @RequestMapping(Setting.PATH_GRANT)
    @ResponseBody
    public Object grant(@RequestParam String id,
                        @RequestParam String access_code,
                        @RequestParam String files,
                        @RequestParam(required = false) Boolean regrant,
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
            //把files转换为List<FileDTO>
            List<FileDTO> fileDTOs = null;
            try {
                fileDTOs = gson.fromJson(files, Setting.TYPE_LIST_FILEDTO);
            } catch (JsonSyntaxException e) {
                return gson.toJson(Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER +
                        ", Cannot convert to List<FileDTO>: " + files));
            }
            //将所有FileDTO添加至Map
            List<FileDTO> accessFileDTOs = userDTO.getContent();
            Map<String, FileDTO> mapFileDTOs = new HashMap<>();
            for (FileDTO fileDTO : accessFileDTOs) {
                mapFileDTOs.put(fileDTO.getId(), fileDTO);
            }
            //尝试从Model中获取regrant
            if (regrant == null) {
                logger.error("Model = {}", gson.toJson(model.asMap())); //TODO
                regrant = Boolean.TRUE.toString().equals(model.asMap().get("regrant"));
                logger.info("regrant from model = " + model.asMap().get("regrant")); //TODO
            }
            //grant & regrant
            for (FileDTO fileDTO : fileDTOs) {
                if (Boolean.TRUE.equals(regrant)) {
                    //regrant
                    if (Util.isEmpty(fileDTO.getId())) continue;
                    mapFileDTOs.remove(fileDTO.getId());
                } else {
                    //grant
                    if (Util.isEmpty(fileDTO.getId()) || Util.isEmpty(fileDTO.getAccess_code())) continue;
                    mapFileDTOs.put(fileDTO.getId(), fileDTO);
                }
            }
            //将Map更新至Content
            userDTO.setContent(new ArrayList<>(mapFileDTOs.values()));
            int rowsAffected = userDatabaseService.updateUserDTO(userDTO, userDTO, conn);
            if (rowsAffected > 0) {
                logger.info((Boolean.TRUE.equals(regrant) ? "Regrant" : "Grant") + " User, UserDTO = {}, Files = {}", gson.toJson(Util.userDTOInfo(userDTO)), files);
            } else {
                logger.info((Boolean.FALSE.equals(regrant) ? "Regrant" : "Grant") + "Error, UserDTO = {}, Files = {}", gson.toJson(Util.userDTOInfo(userDTO)), files);
            }
            return gson.toJson(Util.getResultDO(rowsAffected > 0, rowsAffected));
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 将files转换为List<FileDTO>,并以id为key移除相应FileDTO授权
     *
     * @param id
     * @param access_code
     * @param files
     * @return
     */
    @RequestMapping(Setting.PATH_REGRANT)
    @ResponseBody
    public Object regrant(@RequestParam String id,
                          @RequestParam String access_code,
                          @RequestParam String files,
                          Model model) {
        //实际上这并不会添加到RequestParam中
        model.addAttribute("id", id);
        model.addAttribute("access_code", access_code);
        model.addAttribute("files", files);
        model.addAttribute("regrant", true);
//        return new ModelAndView("forward:" + Setting.URL_GRANT, model.asMap());
        return new ModelAndView("redirect:" + Setting.URL_GRANT, model.asMap());
    }

    @RequestMapping(Setting.PATH_REMOVE)
    @ResponseBody
    public Object remove(@RequestParam String id,
                         @RequestParam String access_code) {
        Connection conn = null;
        UserDTO userDTO = null;
        try {
            conn = userDatabaseService.getConnection();
            //获取UserDTO并作安全检查
            userDTO = userDatabaseService.getUserDTO(id, false, conn);
            if (userDTO == null) {
                return gson.toJson(Util.getResultDO(true, Setting.STATUS_USER_NOT_FOUND, Setting.MESSAGE_USER_NOT_FOUND));
            }
            if (!securityCheck(id, access_code, userDTO)) {
                return gson.toJson(Util.getResultDO(false, Setting.STATUS_ACCESS_DENIED, Setting.MESSAGE_ACCESS_DENIED));
            }
            //删除
            int rowsAffected = userDatabaseService.removeUserDTO(id, conn);
            logger.info("Remove User, UserDTO = {}", gson.toJson(Util.userDTOInfo(userDTO)));
            return gson.toJson(Util.getResultDO(rowsAffected > 0, rowsAffected));
        } finally {
            closeConnection(conn);
        }
    }
}
