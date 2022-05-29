package com.example.demo.src.user;


import com.example.demo.config.BaseException;

import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }


    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인 : 무언가를 체크하는거도 조회의 의미. Dao가 아닌 provider 에서
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try {
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            // TODO: jwt는 다음주차에서 배울 내용입니다!
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try {
            int result = userDao.modifyUserName(patchUserReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저 삭제 API /삭제하고 싶은 유저가 없다면 (?) -- 실패
//    public DeleteUserRes deleteUserByIdx(int userIdx) {
//        Iterator<User> iterator = users.iterator();
//
//        while(iterator.hasNext()){
//            User user = iterator.next();
//
//            if(user.getUserIdx() == userIdx){
//                iterator.remove();
//                return user;
//            }
//        }
//        return null;
//    }

    public void deleteUser(DeleteUserReq deleteUserReq) throws BaseException {
        try {
            int result = userDao.deleteUser(deleteUserReq); //return result 안하나?
            if (result == 0) {  //쿼리문 에러일시 0 반환 .
                throw new BaseException(DELETE_FAIL_USER);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //유저삭제 API 2번째 0528
    public void deleteUserbyIdx(int userIdx) throws BaseException {
        if (userProvider.checkUser(userIdx) == 0) {
            throw new BaseException(DELETE_USER_NOTEXIST);
        }
        try {
            int result = userDao.deleteUserbyIdx(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

