package com.sky.api.rpc.response;

import java.sql.JDBCType;
import com.sky.api.enums.GenderEnum;
import java.io.Serializable;
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

    private String surname;

    /**
     * @see GenderEnum#getValue()
     */
    private GenderEnum gender;

    private JDBCType jdbcType;

}
