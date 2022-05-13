package com.sky.api.rpc.response;

import java.sql.JDBCType;
import com.sky.api.enums.GenderEnum;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gangyf
 * @since 2020/9/6 10:24 PM
 */
@Data
@Builder
@EqualsAndHashCode
public class UserResponse implements Serializable {

    private Long id;

    private String firstName;

    /**
     * 测试基本类型
     */
    private int testInt;

    @NotNull
    @Length(max = 64, min = 1)
    @Size(max = 64, min = 1, message = "长度不能超过64")
    private String surname;

    /**
     * @see GenderEnum#getValue()
     */
    private GenderEnum gender;

    private JDBCType jdbcType;

}
