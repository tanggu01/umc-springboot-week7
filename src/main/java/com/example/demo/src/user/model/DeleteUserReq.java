package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteUserReq {
    private int userIdx;
    //추가
    private String status;

    public DeleteUserReq(int userIdx) {
        this.userIdx = userIdx;
    }
}
