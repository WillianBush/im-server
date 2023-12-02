package com.imservices.im.bmm.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "employee_default_message")
public class EmployeeDefaultMessage implements Serializable {

    private static final long serialVersionUID = -7597374440934077489L;

    @Id
    @Column(length = 32, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    private String employee_id;

    private String member_id;

    private String invite_code;

    private String msg_1;

    private String msg_2;

    private String msg_3;

    private String picture_1;

    private String picture_2;

    private String picture_3;

    private String picture_4;

    private String picture_5;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getMsg_1() {
        return msg_1;
    }

    public void setMsg_1(String msg_1) {
        this.msg_1 = msg_1;
    }

    public String getMsg_2() {
        return msg_2;
    }

    public void setMsg_2(String msg_2) {
        this.msg_2 = msg_2;
    }

    public String getMsg_3() {
        return msg_3;
    }

    public void setMsg_3(String msg_3) {
        this.msg_3 = msg_3;
    }

    public String getPicture_1() {
        return picture_1;
    }

    public void setPicture_1(String picture_1) {
        this.picture_1 = picture_1;
    }

    public String getPicture_2() {
        return picture_2;
    }

    public void setPicture_2(String picture_2) {
        this.picture_2 = picture_2;
    }

    public String getPicture_3() {
        return picture_3;
    }

    public void setPicture_3(String picture_3) {
        this.picture_3 = picture_3;
    }

    public String getPicture_4() {
        return picture_4;
    }

    public void setPicture_4(String picture_4) {
        this.picture_4 = picture_4;
    }

    public String getPicture_5() {
        return picture_5;
    }

    public void setPicture_5(String picture_5) {
        this.picture_5 = picture_5;
    }
}
