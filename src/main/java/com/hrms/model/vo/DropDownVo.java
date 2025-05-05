package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "CascadeDropDownVo")
@Accessors(chain = true)
public class DropDownVo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "String", description = "中文名稱")
    private String name;

    @Schema(description = "對應的值")
    private T value;

    @Schema(description = "關聯的值")
    private T referenceValue;

    public DropDownVo(String name, T value) {
        this.name = name;
        this.value = value;
    }
}
