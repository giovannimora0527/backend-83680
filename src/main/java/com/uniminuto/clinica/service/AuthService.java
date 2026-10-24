package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AuthRq;
import com.uniminuto.clinica.models.AuthRs;

public interface AuthService {

    AuthRs login(AuthRq authRq) throws BadRequestException;
}
