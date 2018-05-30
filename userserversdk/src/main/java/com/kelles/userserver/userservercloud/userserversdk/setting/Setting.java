package com.kelles.userserver.userservercloud.userserversdk.setting;

import com.google.gson.reflect.TypeToken;
import com.kelles.fileserver.fileserversdk.data.FileDTO;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

public class Setting {
    public final static Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    public final static String MYSQL_USER = "root";
    public final static String MYSQL_PASSWORD = "tom44123";
    public final static String MYSQL_REPO_NAME_USERSERVER = "userserver";
    public final static String MYSQL_TABLE_NAME_USERSERVER = "users";
    public final static String MYSQL_URL = "jdbc:mysql://localhost:3306/";

    public final static String TYPE_NOT_SPECIFIED = "TYPE_NOT_SPECIFIED";
    public final static String TYPE_MESSAGE = "TYPE_MESSAGE";

    public final static int STATUS_ERROR = -1;
    public final static int STATUS_FILE_NOT_FOUND = -2;
    public final static int STATUS_INVALID_PARAMETER = -3;
    public final static int STATUS_ACCESS_DENIED = -4;
    public final static int STATUS_FILE_ALREADY_EXISTS = -5;
    public final static int STATUS_PARSE_JSON_ERROR = -6;
    public final static int STATUS_RESPONSE_FAILURE = -7;
    public final static int STATUS_FILE_SIZE_INVALID = -8;
    public final static int STATUS_USER_NOT_FOUND = -2;

    public final static String MESSAGE_FILE_NOT_FOUND = "File Not Found";
    public final static String MESSAGE_ACCESS_DENIED = "Access Denied";
    public final static String MESSAGE_FILE_ALREADY_EXISTS = "File Already Exists";
    public final static String MESSAGE_INVALID_PARAMETER = "Invalid Parameter";
    public final static String MESSAGE_PARSE_JSON_ERROR = "Parse Json Error";
    public final static String MESSAGE_RESPONSE_FAILURE = "Response Failure";
    public final static String MESSAGE_FILE_SIZE_INVALID = "File Size Invalid";
    public final static String MESSAGE_USER_NOT_FOUND = "User Not Found";

    public final static String PATH_UI = "/ui";
    public final static String PATH_INDEX = "/index";
//    public final static String PATH_DISPLAY = "/display";
    public final static String PATH_USER = "/user";
    public final static String PATH_INSERT = "/insert";
    public final static String PATH_GET = "/get";
//    public final static String PATH_UPDATE = "/update";
    public final static String PATH_REMOVE = "/remove";
    public final static String PATH_GRANT = "/grant";
    public final static String PATH_REGRANT = "/regrant";

    public final static String URL_BASIC = "http://119.23.51.183:8081";
//    public final static String URL_BASIC = "http://localhost:8081";
    public final static String URL_INSERT = PATH_USER + PATH_INSERT;
    public final static String URL_GET = PATH_USER + PATH_GET;
//    public final static String URL_UPDATE = PATH_USER + PATH_UPDATE;
    public final static String URL_REMOVE = PATH_USER + PATH_REMOVE;
    public final static String URL_INDEX = PATH_UI + PATH_INDEX;
//    public final static String URL_DISPLAY = PATH_UI + PATH_DISPLAY;
    public final static String URL_GRANT = PATH_USER + PATH_GRANT;
    public final static String URL_REGRANT = PATH_USER + PATH_REGRANT;
    
    public final static Type TYPE_LIST_FILEDTO = new TypeToken<List<FileDTO>>() {}.getType();

    public final static String HEADER_FILEDTO_INFO = "File-Info";
}
