package com.threadvine.service;


import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class TokenBlackListService {

    public Set<String> tokenBlackList = new HashSet<>();


    public void addTokenToBlackList(String token){
        tokenBlackList.add( token );
    }


    public boolean isTokenIsBlockListed(String token){
        return tokenBlackList.contains( token );
    }

}