package com.mase.cafe.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SelfDeleteNotAllowedException extends RuntimeException {
    public SelfDeleteNotAllowedException(String username) {
        super("Users cannot delete themselves: " + username);
    }
}

