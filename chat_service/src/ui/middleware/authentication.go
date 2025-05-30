package middleware

import (
	"chat_service/core/domain/common"
	"chat_service/kernel/apihelper"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"github.com/golibs-starter/golib/log"
)

type JWTConfig struct {
	SecretKey  string
	Expiration time.Duration // in seconds
	Issuer     string
}

func NewJWTConfig() *JWTConfig {
	return &JWTConfig{
		SecretKey:  "MnAYFOKwxBOznHpxA3Wx4cJjeC3vYNtwzI6HRYT9SD++1BM9Pk32pTGETroljFsS",
		Expiration: 8640000,
		Issuer:     "quantuanhuy",
	}
}

type JWTClaims struct {
	Subject string `json:"sub"`
	Scope   string `json:"scope"`
	Email   string `json:"email"`
	jwt.RegisteredClaims
}

func JWTAuthMiddleware(config *JWTConfig) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			log.Error(c, "Authorization header is missing")
			apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
			c.Abort()
			return
		}

		tokenString := strings.Replace(authHeader, "Bearer ", "", 1)
		if tokenString == authHeader {
			log.Error(c, "Invalid token format", nil)
			apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
			c.Abort()
			return
		}
		c.Set("token", tokenString)

		token, err := jwt.ParseWithClaims(tokenString, &JWTClaims{}, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
			}
			return []byte(config.SecretKey), nil
		})

		if err != nil {
			log.Error(c, "Failed to parse JWT token", err)
			apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
			c.Abort()
			return
		}

		if claims, ok := token.Claims.(*JWTClaims); ok && token.Valid {
			userID, err := strconv.ParseInt(claims.Subject, 10, 64)
			if err != nil {
				log.Error(c, "Failed to parse user ID from JWT subject", err)
				apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
				return
			}

			// Set user information in context
			c.Set("userID", userID)
			c.Set("role", claims.Scope)
			c.Next()
		} else {
			log.Error(c, "Invalid JWT token", nil)
			apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
			c.Abort()
			return
		}
	}
}

func RoleAuthorization(roles ...string) gin.HandlerFunc {
	return func(c *gin.Context) {
		userScope, exists := c.Get("role")
		if !exists {
			log.Error(c, "User scope not found in context", nil)
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			c.Abort()
			return
		}

		scopeStr, ok := userScope.(string)
		if !ok {
			log.Error(c, "User scope is not a string", nil)
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			c.Abort()
			return
		}

		userRoles := strings.Split(scopeStr, " ")
		for _, requiredRole := range roles {
			for _, userRole := range userRoles {
				if requiredRole == userRole {
					c.Next()
					return
				}
			}
		}

		log.Error(c, "User doesn't have required role ", roles)
		apihelper.AbortErrorHandle(c, common.GeneralForbidden)
		c.Abort()
	}
}
