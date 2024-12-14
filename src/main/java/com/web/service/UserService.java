package com.web.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.web.dto.CustomUserDetails;
import com.web.dto.TokenDto;
import com.web.dto.UserRequest;
import com.web.dto.UserUpdate;
import com.web.entity.User;
import com.web.enums.UserType;
import com.web.exception.MessageException;
import com.web.jwt.JwtTokenProvider;
import com.web.repository.AuthorityRepository;
import com.web.repository.UserRepository;
import com.web.utils.Contains;
import com.web.utils.MailService;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.*;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public TokenDto login(String email, String password) throws Exception {
        Optional<User> users = userRepository.findByEmail(email);
        if(users.isPresent()){
            if(users.get().getUserType().equals(UserType.GOOGLE)){
                throw new MessageException("Hãy đăng nhập bằng google");
            }
        }
        // check infor user
        checkUser(users);
        if(passwordEncoder.matches(password, users.get().getPassword())){
            CustomUserDetails customUserDetails = new CustomUserDetails(users.get());
            String token = jwtTokenProvider.generateToken(customUserDetails);
            TokenDto tokenDto = new TokenDto();
            tokenDto.setToken(token);
            tokenDto.setUser(users.get());
            return tokenDto;
        }
        else{
            throw new MessageException("Mật khẩu không chính xác", 400);
        }
    }


    public TokenDto loginWithGoogle(GoogleIdToken.Payload payload) throws Exception {
        User user = new User();
        user.setEmail(payload.getEmail());
        user.setAvatar(payload.get("picture").toString());
        user.setFullName(payload.get("name").toString());
        user.setActived(true);
        user.setAuthorities(authorityRepository.findByName(Contains.ROLE_USER));
        user.setCreatedDate(new Date(System.currentTimeMillis()));
        user.setUserType(UserType.GOOGLE);

        Optional<User> users = userRepository.findByEmail(user.getEmail());
        // check infor user

        if(users.isPresent()){
            if(users.get().getActived() == false){
                throw new MessageException("Tài khoản đã bị khóa");
            }
            CustomUserDetails customUserDetails = new CustomUserDetails(users.get());
            String token = jwtTokenProvider.generateToken(customUserDetails);
            TokenDto tokenDto = new TokenDto();
            tokenDto.setToken(token);
            tokenDto.setUser(users.get());
            return tokenDto;
        }
        else{
            User u = userRepository.save(user);
            CustomUserDetails customUserDetails = new CustomUserDetails(u);
            String token = jwtTokenProvider.generateToken(customUserDetails);
            TokenDto tokenDto = new TokenDto();
            tokenDto.setToken(token);
            tokenDto.setUser(u);
            return tokenDto;
        }
    }

    public Boolean checkUser(Optional<User> users){
        if(users.isPresent() == false){
            throw new MessageException("Không tìm thấy tài khoản", 404);
        }
        else if(users.get().getActivation_key() != null && users.get().getActived() == false){
            throw new MessageException("Tài khoản chưa được kích hoạt", 300);
        }
        else if(users.get().getActived() == false && users.get().getActivation_key() == null){
            throw new MessageException("Tài khoản đã bị khóa", 500);
        }
        return true;
    }

    public User regisUser(UserRequest userRequest) {
        userRepository.findByEmail(userRequest.getEmail())
                .ifPresent(exist->{
                    if(exist.getActivation_key() != null){
                        throw new MessageException("Tài khoản chưa được kích hoạt", 330);
                    }
                    throw new MessageException("Email đã được sử dụng", 400);
                });
        User user = new User();
        user.setUserType(UserType.EMAIL);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setAuthorities(authorityRepository.findByName(Contains.ROLE_USER));
        user.setActived(false);
        user.setEmail(userRequest.getEmail());
        user.setFullName(userRequest.getFullName());
        user.setCreatedDate(new Date(System.currentTimeMillis()));
        user.setActivation_key(userUtils.randomKey());
        User result = userRepository.save(user);
        mailService.sendEmail(user.getEmail(), "Xác nhận tài khoản của bạn","Cảm ơn bạn đã tin tưởng và xử dụng dịch vụ của chúng tôi:<br>" +
                "Để kích hoạt tài khoản của bạn, hãy nhập mã xác nhận bên dưới để xác thực tài khoản của bạn<br><br>" +
                "<a style=\"background-color: #2f5fad; padding: 10px; color: #fff; font-size: 18px; font-weight: bold;\">"+user.getActivation_key()+"</a>",false, true);
        return result;
    }

    public void activeAccount(String activationKey, String email) {
        Optional<User> user = userRepository.getUserByActivationKeyAndEmail(activationKey, email);
        user.ifPresent(exist->{
            exist.setActivation_key(null);
            exist.setActived(true);
            userRepository.save(exist);
            return;
        });
        if(user.isEmpty()){
            throw new MessageException("email hoặc mã xác nhận không chính xác", 404);
        }
    }


    public void guiYeuCauQuenMatKhau(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            if(user.get().getUserType().equals(UserType.GOOGLE)){
                throw new MessageException("Tài khoản đăng nhập bằng google, không thể thực hiện chức năng này");
            }
        }
        checkUser(user);
        String random = userUtils.randomKey();
        user.get().setRememberKey(random);
        userRepository.save(user.get());

        mailService.sendEmail(email, "Đặt lại mật khẩu","Cảm ơn bạn đã tin tưởng và xử dụng dịch vụ của chúng tôi:<br>" +
                "Chúng tôi đã tạo một mật khẩu mới từ yêu cầu của bạn<br>" +
                "Hãy lick vào bên dưới để đặt lại mật khẩu mới của bạn<br><br>" +
                "<a href='http://localhost:8080/datlaimatkhau?email="+email+"&key="+random+"' style=\"background-color: #2f5fad; padding: 10px; color: #fff; font-size: 18px; font-weight: bold;\">Đặt lại mật khẩu</a>",false, true);

    }

    public void xacNhanDatLaiMatKhau(String email, String password, String key) {
        Optional<User> user = userRepository.findByEmail(email);
        checkUser(user);
        if(user.get().getRememberKey().equals(key)){
            user.get().setPassword(passwordEncoder.encode(password));
            userRepository.save(user.get());
        }
        else{
            throw new MessageException("Mã xác thực không chính xác");
        }
    }

    public void updateInfor(UserUpdate userUpdate){
        User user = userUtils.getUserWithAuthority();
        user.setFullName(userUpdate.getFullName());
        user.setAvatar(userUpdate.getAvatar());
        userRepository.save(user);
    }

    public void changePass(String oldPass, String newPass) {
        User user = userUtils.getUserWithAuthority();
        if(user.getUserType().equals(UserType.GOOGLE)){
            throw new MessageException("Xin lỗi, chức năng này không hỗ trợ đăng nhập bằng google");
        }
        if(passwordEncoder.matches(oldPass, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(user);
        }
        else{
            throw new MessageException("Mật khẩu cũ không chính xác", 500);
        }
    }

    public List<User> getUserByRole(String role) {
        if(role == null){
            return userRepository.findAll();
        }
        return userRepository.getUserByRole(role);
    }
}
