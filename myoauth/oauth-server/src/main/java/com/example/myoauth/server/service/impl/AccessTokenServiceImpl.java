package com.example.myoauth.server.service.impl;

import com.example.base.rest.Rest;
import com.example.myoauth.common.constant.StatusConstans;
import com.example.myoauth.common.manage.AccessTokenManage;
import com.example.myoauth.common.manage.ClientDetailsManage;
import com.example.myoauth.common.manage.RefreshTokenManage;
import com.example.myoauth.dto.AccessTokenDTO;
import com.example.myoauth.dto.ClientDetailsDTO;
import com.example.myoauth.dto.RefreshTokenDTO;
import com.example.myoauth.server.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    /**
     * token 长度
     */
    private final int tokenLength = 18;

    @Autowired
    private AccessTokenManage accessTokenManage;

    @Autowired
    private RefreshTokenManage refreshTokenManage;

    @Autowired
    private ClientDetailsManage clientDetailsManage;

    @Override
    public Rest createAccessToken(String clientKey, String clientSecret) {
        ClientDetailsDTO clientDetailsDTO = clientDetailsManage.getClientDetailsByClientKey(clientKey);
        if (clientDetailsDTO == null)
            return Rest.failure(StatusConstans.AUTHENTICATION_ERROR, "key not find!");
        if (!clientDetailsDTO.getClientSecret().equals(clientSecret))
            return Rest.failure(StatusConstans.AUTHENTICATION_ERROR, "secret not match to key");

        //先声明accessToken 和refreshToken 如果未过期，则直接赋值
        AccessTokenDTO accessTokenDTO = null;
        RefreshTokenDTO refreshTokenDTO = null;
        Date currentTime = new Date();
        RefreshTokenDTO oldRefreshToken = refreshTokenManage.listRefreshTokenByClientKey(clientKey);
        if (oldRefreshToken != null) {
            int different = calculateTimeBalance(currentTime, oldRefreshToken.getCreateTime());
            //refreshToken 已经存在并且没有过时的情况
            if (different <= oldRefreshToken.getExpiresTime()) {
                refreshTokenDTO = oldRefreshToken;
                AccessTokenDTO oldAccessToken = accessTokenManage.getAccessTokenByClientKey(clientKey);
                if (oldAccessToken != null) {
                    int def = calculateTimeBalance(currentTime, oldAccessToken.getCreateTime());
                    //accessToken 存在未过期
                    if (def < oldAccessToken.getExpiresTime()) {
                        oldAccessToken.setCreateTime(currentTime);
                        accessTokenDTO = oldAccessToken;
                    }
                }
            }
        }
        String refreshTokenStr = createRefreshToken();
        String accessTokenStr = refreshTokenStr.substring(0, tokenLength);
        //创建新的 refreshToken
        //不存在更新refreshToken的情况
        if (refreshTokenDTO == null) {
            refreshTokenDTO = new RefreshTokenDTO();
            refreshTokenDTO.setClientKey(clientKey);
            refreshTokenDTO.setCreateTime(new Date());
            refreshTokenDTO.setRefreshToken(refreshTokenStr);
            refreshTokenDTO.setExpiresTime(clientDetailsDTO.getRefreshTokenValidity());
            refreshTokenDTO = refreshTokenManage.saveRefreshToken(refreshTokenDTO);
        }


        //创建新的 accessToken
        if (accessTokenDTO == null) {
            accessTokenDTO = new AccessTokenDTO();
            accessTokenDTO.setClientKey(clientDetailsDTO.getClientKey());
            accessTokenDTO.setCreateTime(currentTime);
            accessTokenDTO.setToken(accessTokenStr);
            accessTokenDTO.setRefreshTokenId(refreshTokenDTO.getRefreshTokenId());
            accessTokenDTO.setExpiresTime(clientDetailsDTO.getAccessTokenValidity());
            accessTokenManage.saveAccessToken(accessTokenDTO);
        } else {
            //有已经存在的accessToken,更新下创建时间即可
            accessTokenManage.updateAccessTokenCreateTime(accessTokenDTO);
        }

        return Rest.success(accessTokenDTO, StatusConstans.SUCCESS);
    }

    private String createRefreshToken() {
        String refreshToken = UUID.randomUUID().toString();
        return refreshToken.replace("-", "");
    }

    /**
     * 获取两个时间的时间差 单位秒
     *
     * @param current
     * @param previous
     * @return
     */
    private int calculateTimeBalance(Date current, Date previous) {
        return (int) (current.getTime() - previous.getTime()) / 1000;
    }


}
