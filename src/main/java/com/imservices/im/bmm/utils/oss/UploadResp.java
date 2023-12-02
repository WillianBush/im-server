package com.imservices.im.bmm.utils.oss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResp {
    private String filePath;
    private String md5Key;
}
