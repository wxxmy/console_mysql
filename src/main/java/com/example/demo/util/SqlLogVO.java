package com.example.demo.util;

import lombok.Data;

/**
 * desc:
 *
 * @author : ckl
 * creat_date: 2019/12/31 0031
 * creat_time: 14:13
 **/
@Data
public class SqlLogVO {
    private String prepareSqlStr;

    private String parameterStr;

    private String completeSqlStr;
}
