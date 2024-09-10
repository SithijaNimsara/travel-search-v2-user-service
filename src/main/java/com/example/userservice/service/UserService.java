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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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
            Optional<User> userOptional= userRepository.findById(id);
            if(userOptional.isPresent()) {
                User userInfo = userOptional.get();
                UserProfileResponseDto userProfileResponseDto = UserProfileResponseDto.builder()
                        .name(userInfo.getName())
                        .email(userInfo.getEmail())
                        .address(userInfo.getAddress())
                        .state(userInfo.getState())
                        .country(userInfo.getCountry())
                        .image(userInfo.getImage())
                        .role(userInfo.getRole())
                        .build();
                return new ResponseEntity<>(userProfileResponseDto, HttpStatus.OK);
            } else {
                logger.error("User profile not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (Exception  e) {
            logger.error("Error retrieving user profile: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> saveUser(MultipartFile image, UserNoImageRequestDto createUser) {
        try {
            if(userRepository.findByName(createUser.getName()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
            String passwordValidationMessage = validatePassword(createUser.getPassword());
            if (passwordValidationMessage != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(passwordValidationMessage);
            }
            if (userRepository.findByEmail(createUser.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }
            if (image == null || image.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is required");
            }
            if (image.getSize() > 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is too large. Max size is 1 MB");
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
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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
            logger.info("Login successful");
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", loginUserDto.getName());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Exception- {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseDto<UserSearchResponseDto> searchBusinessUser(String name, int page) {
        try {
            Pageable pageable = PageRequest.of(page, 5);
            Page<Object[]> pageObj = userRepository.getBusinessUserByName(name, pageable);

            if (pageObj.isEmpty()) {
                logger.warn("No business users found with name: {}", name);
                return new ResponseDto<>(Collections.emptyList(), new PaginationDetailsDto(0, page, 5));
            }

            List<UserSearchResponseDto> dtos = pageObj.stream()
                    .map(record -> new UserSearchResponseDto(
                            ((Number) record[0]).intValue(),
                            (String) record[1],
                            (byte[]) record[2]
                    ))
                    .collect(Collectors.toList());

            paginationDetailsDto = new PaginationDetailsDto(pageObj.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
            return new ResponseDto<>(dtos, paginationDetailsDto);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return new ResponseDto<>(null, new PaginationDetailsDto(0, 0, 0));
        } catch (DataAccessException e) {
            logger.error("Database error occurred while searching for business users: {}", e.getMessage());
            return new ResponseDto<>(null, new PaginationDetailsDto(0, 0, 0));
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseDto<>(null, new PaginationDetailsDto(0, 0, 0));
        }
    }

    public ResponseEntity<UserDto> getUserByPostId(int postId) {
        try {
            User user = userRepository.findById(postId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Set<PostDto> postDtoSet = user.getUserPosts().stream()
                    .map(post -> new PostDto(post.getPostId(),post.getCaption(), post.getTime(), post.getImage(), post.getHotelId(), post.getPostUsers()))
                    .collect(Collectors.toSet());

            UserDto userDto = new UserDto(user.getUserId(), user.getName(),
                    user.getEmail(), user.getPassword(), user.getAddress(),
                    user.getState(), user.getCountry(), user.getRole(),
                    user.getImage(), postDtoSet);

            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            logger.error("UsernameNotFoundException- {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error("Exception- {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<User> saveLikedUser(UserDto userDto) {
        User user;
        try {
            Set<Post> postSet = userDto.getUserPosts().stream()
                    .map(postDto -> new Post(postDto.getPostId(), postDto.getCaption(), postDto.getTime(),
                            postDto.getImage(), postDto.getHotelId(), postDto.getPostUsers(), new HashSet<>()))
                    .collect(Collectors.toSet());

            user = new User(userDto.getUserId(), userDto.getName(), userDto.getEmail(),
                    userDto.getPassword(), userDto.getAddress(), userDto.getState(),
                    userDto.getCountry(), userDto.getRole(), userDto.getImage(),
                    postSet);
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }  catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation: {}", ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid input: {}", ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An unexpected error occurred: {}", ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Password validation method
    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit";
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character";
        }
        return null; // Password is valid
    }

}
