package com.sky.api.rpc.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author gangyf
 * @since 2020/10/10 2:31 PM
 */
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest implements Serializable {

    private Long id;

    private String firstName;

    private String surname;

}
