package com.kelles.userserver.userservercloud.userserversdk.setting;

public class SQL {
    public final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + Setting.MYSQL_TABLE_NAME_USERSERVER + "(\n" +
            "id VARCHAR(150) PRIMARY KEY,\n" +
            "access_code VARCHAR(150) NOT NULL,\n" +
            "create_time BIGINT,\n" +
            "content BLOB\n" +
            ") DEFAULT CHARSET=utf8;";
    public final static String INSERT = "INSERT INTO " + Setting.MYSQL_TABLE_NAME_USERSERVER + "\n" +
            "(id,access_code,create_time,content)\n" +
            "VALUES\n" +
            "(?,?,?,?);";
    public final static String DELETE = "DELETE FROM " + Setting.MYSQL_TABLE_NAME_USERSERVER + " WHERE id=?;";
    public final static String SELECT = "SELECT access_code,create_time,content FROM " + Setting.MYSQL_TABLE_NAME_USERSERVER + " WHERE id=?;";
    public final static String SELECT_NO_CONTENT = "SELECT access_code,create_time FROM " + Setting.MYSQL_TABLE_NAME_USERSERVER + " WHERE id=?;";
    public final static String UPDATE_INFO = "UPDATE " + Setting.MYSQL_TABLE_NAME_USERSERVER + " SET access_code=?,create_time=? WHERE id=?;";
    public final static String UPDATE_CONTENT = "UPDATE " + Setting.MYSQL_TABLE_NAME_USERSERVER + " SET content=? WHERE id=?;";
}
