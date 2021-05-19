package com.rpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author slorui
 * data 2021/5/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject {
    private Integer id;
    private String message;

}
