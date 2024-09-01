package com.example.userservice.service;

import com.example.userservice.dto.*;

import com.example.userservice.entity.Post;
import com.example.userservice.entity.User;
import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PaginationDetailsDto paginationDetailsDto;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ResponseEntity<UserProfileResponseDto> getUserById(int id) {
        try {
            User userInfor= userRepository.findById(id).get();
            UserProfileResponseDto userProfileResponseDto = UserProfileResponseDto.builder()

                    .name(userInfor.getName())
                    .email(userInfor.getEmail())
                    .address(userInfor.getAddress())
                    .state(userInfor.getState())
                    .country(userInfor.getCountry())
                    .image(userInfor.getImage())
                    .role(userInfor.getRole())
                    .build();
            return new ResponseEntity<>(userProfileResponseDto, HttpStatus.OK);
        }catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity saveUser(MultipartFile image, UserNoImageRequestDto createUser) {
        try {
            if(userRepository.findByName(createUser.getName()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
            if (userRepository.findByEmail(createUser.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }
            User user = User.builder()
                    .name(createUser.getName())
                    .email(createUser.getEmail())
//                    .password(createUser.getPassword())
                    .password(passwordEncoder.encode(createUser.getPassword()))
                    .address(createUser.getAddress())
                    .state(createUser.getState())
                    .country(createUser.getCountry())
                    .role(createUser.getRole())
                    .image(image.getBytes())
                    .build();
            userRepository.save(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<JwtResponse> loginUser(LoginUserDto loginUserDto) {

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDto.getName(), loginUserDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserInforDto user = (UserInforDto) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(user.getUserId(),
                    jwt,
                    user.getName(),
                    user.getRole());
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
//            return ResponseEntity.ok(new JwtResponse(user.getUserId(),
//                    jwt,
//                    user.getName(),
//                    user.getRole()));
        } catch (Exception e) {
//            ErrorDto errorDto = new ErrorDto();
//            errorDto.setStatus(401);
//            errorDto.setMessage("Bad credentials");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseDto<UserSearchResponseDto> searchBusinessUser(String name, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Object[]> pageObj = userRepository.getBusinessUserByName(name, pageable);

        List<UserSearchResponseDto> dtos = pageObj.stream()
                .map(record -> new UserSearchResponseDto(
                        ((Number) record[0]).intValue(),
                        (String) record[1],
                        (byte[]) record[2]
                ))
                .collect(Collectors.toList());

        paginationDetailsDto = new PaginationDetailsDto(pageObj.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseDto<>(dtos, paginationDetailsDto);
    }

    public ResponseEntity<UserDto> getUserByPostId(int postId) {
        User user = userRepository.findById(postId).orElse(null);

        Set<PostDto> postDtoSet = user.getUserPosts().stream()
                .map(post -> new PostDto(post.getPostId(),post.getCaption(), post.getTime(), post.getImage(), post.getHotelId(), post.getPostUsers()))
                .collect(Collectors.toSet());

        UserDto userDto = new UserDto(user.getUserId(), user.getName(),
                user.getEmail(), user.getPassword(), user.getAddress(),
                user.getState(), user.getCountry(), user.getRole(),
                user.getImage(), postDtoSet);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
//        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> saveLikedUser(UserDto userDto) {
        logger.info("Service "+userDto.toString());
        Set<Post> postSet = userDto.getUserPosts().stream()
                .map(postDto -> new Post(postDto.getPostId(), postDto.getCaption(), postDto.getTime(),
                postDto.getImage(), postDto.getHotelId(), postDto.getPostUsers(), new HashSet<>()))
                .collect(Collectors.toSet());

        User user = new User(userDto.getUserId(), userDto.getName(), userDto.getEmail(),
                userDto.getPassword(), userDto.getAddress(), userDto.getState(),
                userDto.getCountry(), userDto.getRole(), userDto.getImage(),
                postSet);
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }




}
