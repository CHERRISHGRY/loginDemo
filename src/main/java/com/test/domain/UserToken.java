package com.test.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.sql.DataSourceDefinition;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by Luo_xuri on 2017/9/30.
 */
@Entity
@Table(name = "user_token")
@Data
@NoArgsConstructor
public class UserToken {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 512)
    private String token;

    @JsonFormat(pattern = "yy-MM-dd hh:mm:ss")
    private Date createtime = new Date();

    public UserToken(String token){
        this.token = token;
    }
}
