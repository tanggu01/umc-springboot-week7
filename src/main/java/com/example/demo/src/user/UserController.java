package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
//유저 도메인
@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<GetUserRes>
     */
    //Query String - @RequestParam
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<GetUserRes> getUsers(@RequestParam(required = true) String Email) { //<GetUserRes> model: 응답값.
        //Model에서는 필요한 요청값/응답값 형식을 정리해놓는다. 어떠한 형태/어떠한 데이터를 출력할건지/클라에게 전달할건지 정의를 해주는곳
        try {
            // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
            if (Email.length() == 0) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            // 이메일 정규표현
            if (!isRegexEmail(Email)) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            GetUserRes getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //툭정 유저 조회 API
    //- [GET] /users/:userIdx
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx") int userIdx) {
        try {
            GetUserRes getUsersRes = userProvider.getUsersByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    //0528 유저 삭제 API
    @ResponseBody
    @DeleteMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> deleteUserByIdx(@PathVariable("userIdx") int userIdx) {
        try {
//            DeleteUserRes deleteUserRes = userProvider.DeleteUsersByIdx(userIdx);

            DeleteUserReq deleteUserReq = new DeleteUserReq(userIdx);
            userService.deleteUserbyIdx(userIdx);

            String result = " 유저가 삭제되었습니다.";
            return new BaseResponse<>(result); //이 Result는 어디에서 온거지?

//            return new BaseResponse<>(deleteUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     *
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/users
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
            /* TODO: jwt는 다음주차에서 배울 내용입니다!
            jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            */
            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickName());
            userService.modifyUserName(patchUserReq);

            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //유저 삭제 API: status 만 inactive 로 바꿔주기. PATCH? 위에와 비슷하게 하는데 왜 Delete는 Res 가 있고 위에꺼는 없는지.
    @ResponseBody
    @PatchMapping("/delete/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx, @RequestBody DeleteUserReq user) { //클라이언트에게 보내줄정보
        try {
            DeleteUserReq deleteUserReq = new DeleteUserReq(userIdx, user.getStatus());
//            if (deleteUserReq.getStatus().equals("Inactive")) {
//                return new BaseResponse<>(DELETE_USER_NOTEXIST);
//            }
            userService.deleteUser(deleteUserReq);

            //이부분 두줄은 무슨 뜻?
            String result = " 유저가 삭제되었습니다.";
            return new BaseResponse<>(result); //이 Result는 어디에서 온거지?

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    //유저삭제 2
//    @ResponseBody
//    @DeleteMapping("/{userIdx}/status") // (PATCH) 127.0.0.1:9000/users/:userIdx
//    public BaseResponse<DeleteUserRes> deleteUserbyIdx(@PathVariable("userIdx") int userIdx) { //<String?> 안에 뭐가 들어가야하는지??
//        try {
//
//            userService.deleteUserbyIdx(userIdx);
//            return new BaseResponse<>(DELETE_USER_NOTEXIST);
//
//            //3
////            DeleteUserRes deleteUserRes = userService.deleteUserbyIdx(userIdx);
////            return new BaseResponse<>(deleteUserRes);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

}
