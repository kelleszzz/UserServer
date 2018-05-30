package com.kelles.userserver.userservercloud.userserversdk.data;

import com.kelles.fileserver.fileserversdk.data.FileDTO;

import java.io.InputStream;
import java.util.List;

public class UserDTO {
    String id;
    String access_code;
    Long create_time;
    List<FileDTO> content;
    /**
     * InputStream并不直接暴露于调用者,而是通过content来进行转换
     */
    InputStream inputStream;

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public List<FileDTO> getContent() {
        return content;
    }

    public void setContent(List<FileDTO> content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccess_code() {
        return access_code;
    }

    public void setAccess_code(String access_code) {
        this.access_code = access_code;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
