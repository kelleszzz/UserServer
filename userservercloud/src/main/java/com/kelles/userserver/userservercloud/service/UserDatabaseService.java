package com.kelles.userserver.userservercloud.service;

import com.kelles.fileserver.fileserversdk.data.FileDTO;
import com.kelles.userserver.userservercloud.userserversdk.data.UserDTO;
import com.kelles.userserver.userservercloud.userserversdk.setting.*;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class UserDatabaseService extends DatabaseService {
    @PostConstruct
    void init() {
        super.init();
        Connection conn = getConnection();
        try {
            //建表
            try {
                PreparedStatement ps = conn.prepareStatement(SQL.CREATE_TABLE);
                int rowsAffected = ps.executeUpdate();
                logSQLMessage("Create table " + rowsAffected);
                ps.close();
            } catch (SQLException e) {
                logSQLMessage("Create table Error", SQL.CREATE_TABLE);
                e.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logSQLMessage("Close connection Error", true);
                    e.printStackTrace();
                }
            }
        }
    }

    public Connection getConnection() {
        return getConnection(Setting.MYSQL_REPO_NAME_USERSERVER);
    }

    /**
     * @param userDTO 不需要InputStream,自动将Content转换为InputStream
     * @param accessUserDTO 传入时,不再执行一次SELECT
     * @param conn
     * @return
     */
    public int updateUserDTO(UserDTO userDTO, UserDTO accessUserDTO, Connection conn) {
        if (userDTO == null || Util.isEmpty(userDTO.getId()) || conn == null)
            return Setting.STATUS_INVALID_PARAMETER;
        boolean success = false;
        PreparedStatement psUpdateInfo = null, psUpdateContent = null;
        int rowsAffected = Setting.STATUS_ERROR;
        try {
            //查找原文件
            if (accessUserDTO == null || Util.isEmpty(accessUserDTO.getId()) || !accessUserDTO.getId().equals(userDTO.getId())) {
                accessUserDTO = getUserDTO(userDTO.getId(), false, conn);
            }
            if (accessUserDTO == null) return Setting.STATUS_FILE_NOT_FOUND;
            if (!userDTO.getId().equals(accessUserDTO.getId())) return Setting.STATUS_ACCESS_DENIED;
            //将Content转换至InputStream
            if (userDTO.getContent() != null) {
                if (!Util.convertContentIntoInputStream(userDTO, gson)) {
                    logSQLMessage("Update Error, convertContentIntoInpusStream Error, userDTO = " + gson.toJson(Util.userDTOInfo(userDTO)), SQL.INSERT, true);
                    return Setting.STATUS_ERROR;
                }
            }
            //TODO 更新域
            if (userDTO != accessUserDTO) {
                Util.updateDTO(userDTO, accessUserDTO);
            }
            //更新信息
            psUpdateInfo = conn.prepareStatement(SQL.UPDATE_INFO);
            psUpdateInfo.setString(1, accessUserDTO.getAccess_code());
            psUpdateInfo.setLong(2, accessUserDTO.getCreate_time());
            psUpdateInfo.setString(3, accessUserDTO.getId());
            rowsAffected = psUpdateInfo.executeUpdate();
            //更新内容
            if (accessUserDTO.getInputStream() != null) {
                psUpdateContent = conn.prepareStatement(SQL.UPDATE_CONTENT);
                psUpdateContent.setBinaryStream(1, accessUserDTO.getInputStream());
                psUpdateContent.setString(2, accessUserDTO.getId());
                rowsAffected = psUpdateContent.executeUpdate();
            }
            logSQLMessage("Update " + gson.toJson(Util.userDTOInfo(userDTO)), SQL.UPDATE_INFO);
            return rowsAffected;
        } catch (SQLException e) {
            logSQLMessage("Update Error, userDTO = " + gson.toJson(Util.userDTOInfo(userDTO)), SQL.INSERT, true);
            e.printStackTrace();
            return Setting.STATUS_ERROR;
        } finally {
            closePreparedStatement(psUpdateInfo);
            closePreparedStatement(psUpdateContent);
        }
    }

    /**
     * userDTO不需要包含InputStream,只需要包含content
     * content为空时,自动转换为空数组
     *
     * @param userDTO
     * @param conn
     * @return
     */
    public int insertUserDTO(UserDTO userDTO, Connection conn) {
        if (!securityCheck(userDTO) || conn == null) return Setting.STATUS_INVALID_PARAMETER;
        if (Util.isEmpty(userDTO.getContent())) {
            //自动转换出空数组
            userDTO.setContent(new ArrayList<FileDTO>());
        }
        //将Content转换至InputStream
        if (!Util.convertContentIntoInputStream(userDTO, gson)) {
            logSQLMessage("Insert Error, convertContentIntoInpusStream Error, userDTO = " + gson.toJson(Util.userDTOInfo(userDTO)), SQL.INSERT, true);
            return Setting.STATUS_ERROR;
        }
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL.INSERT);
            ps.setString(1, userDTO.getId());
            ps.setString(2, userDTO.getAccess_code());
            ps.setLong(3, System.currentTimeMillis());
            if (userDTO.getInputStream() != null) {
                ps.setBinaryStream(4, userDTO.getInputStream());
            }
            int rowsAffected = ps.executeUpdate();
            logSQLMessage("Insert " + gson.toJson(Util.userDTOInfo(userDTO)), SQL.INSERT);
            return rowsAffected;
        } catch (SQLException e) {
            //当id重复或文件过大时,抛出此异常
            logSQLMessage("Insert Error, userDTO = " + gson.toJson(Util.userDTOInfo(userDTO)), SQL.INSERT, true);
            e.printStackTrace();
//            return Setting.STATUS_ERROR;
            return Setting.STATUS_FILE_ALREADY_EXISTS;
        } finally {
            closePreparedStatement(ps);
        }
    }

    public UserDTO getUserDTO(String id, boolean getContent, Connection conn) {
        if (Util.isEmpty(id) || conn == null) return null;
        PreparedStatement ps = null;
        try {
            if (getContent) ps = conn.prepareStatement(SQL.SELECT);
            else ps = conn.prepareStatement(SQL.SELECT_NO_CONTENT);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(id);
                userDTO.setAccess_code(rs.getString("access_code"));
                userDTO.setCreate_time(rs.getLong("create_time"));
                if (getContent) {
                    userDTO.setInputStream(rs.getBinaryStream("content"));
                    if (!Util.convertInputStreamIntoContent(userDTO, gson)) {
                        throw new SQLException("Cannot convert InputStream to Content");
                    }
                    //TODO
                    userDTO.getInputStream().close();
                    userDTO.setInputStream(null);
                }
                return userDTO;
            }
            return null;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            logSQLMessage("Select Error, id = " + id, getContent ? SQL.SELECT : SQL.SELECT_NO_CONTENT, true);
            return null;
        } finally {
            closePreparedStatement(ps);
        }
    }

    public int removeUserDTO(String id, Connection conn) {
        if (Util.isEmpty(id) || conn == null) return Setting.STATUS_ERROR;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL.DELETE);
            ps.setString(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
            logSQLMessage("Remove Error, id = " + id, SQL.DELETE, true);
            return Setting.STATUS_ERROR;
        } finally {
            closePreparedStatement(ps);
        }
    }

}
