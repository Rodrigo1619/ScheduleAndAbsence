package com.masferrer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.AuthenticationResponse;
import com.masferrer.models.dtos.LoginDTO;
import com.masferrer.models.dtos.RegisterDTO;
import com.masferrer.models.dtos.UserDTO;
import com.masferrer.services.UserService;
import com.masferrer.utils.ExistExceptions;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.UserInactiveException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    public UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO userInfo, BindingResult validations){
        if(validations.hasErrors()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try{
            UserDTO user = userService.register(userInfo);
            if(user == null){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch(ExistExceptions e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO userInfo, BindingResult validations) {
    if (validations.hasErrors()) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    try {
        AuthenticationResponse response = userService.login(userInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }catch (UserInactiveException e) {
        return new ResponseEntity<>("User is inactive", HttpStatus.FORBIDDEN);
    } catch (BadCredentialsException e) {
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    } catch (UsernameNotFoundException e) {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
    

