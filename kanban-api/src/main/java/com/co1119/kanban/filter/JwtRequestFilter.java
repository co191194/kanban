package com.co1119.kanban.filter;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.co1119.kanban.constant.SecurityConst;
import com.co1119.kanban.service.KanbanUserDetailsService;
import com.co1119.kanban.utility.JwtTokenUtility;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final KanbanUserDetailsService kanbanUserDetailsService;
    private final JwtTokenUtility jwtTokenUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // リクエストヘッダーから認証情報を取得
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // ヘッダーが存在し、かつ、指定文字列で始まる場合
        // トークンからユーザ名を取得する
        if (Objects.nonNull(requestTokenHeader)
                && requestTokenHeader.startsWith(SecurityConst.AUTHORIZATION_HEADER_PREFIX)) {
            jwtToken = requestTokenHeader.substring(SecurityConst.AUTHORIZATION_HEADER_PREFIX.length());

            try {
                username = jwtTokenUtility.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.warn("JWTトークンを取得できません。");
            } catch (ExpiredJwtException e) {
                logger.warn("JWTトークンが有効期限切れです。");
            }
        } else {
            // ログインリクエストなどを通すために、トークンがなくても処理を継続する
        }

        // トークンからユーザ名を取得でき、かつ、まだ認証されていない場合で
        // トークンが有効ならコンテキストに認証情報を設定する
        if (Objects.nonNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = kanbanUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtility.validateToken(jwtToken, userDetails)) {
                // 認証トークンを作成してコンテキストに設定する
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }

        filterChain.doFilter(request, response);
    }

}
