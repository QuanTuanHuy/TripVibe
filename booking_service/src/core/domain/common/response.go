package common

import "net/http"

const (
	// GeneralServiceUnavailable General
	GeneralServiceUnavailable = 500

	// GeneralBadRequest General
	GeneralBadRequest = 400

	//GeneralUnauthorized error
	GeneralUnauthorized = 401

	//GeneralForbidden error
	GeneralForbidden = 403

	//ErrBadRequest error
	ErrCodeAccommodationNotFound = 1001
)

const (
	AccommodationNotFoundMessage = "accommodation not found"
)

// ErrorResponse error response struct
type ErrorResponse struct {
	HTTPCode    int
	ServiceCode int
	Message     string
}

var errorResponseMap = map[int]ErrorResponse{
	GeneralServiceUnavailable: {
		HTTPCode:    http.StatusInternalServerError,
		ServiceCode: GeneralServiceUnavailable,
		Message:     "Service unavailable",
	},
	GeneralBadRequest: {
		HTTPCode:    http.StatusBadRequest,
		ServiceCode: GeneralBadRequest,
		Message:     "Bad request",
	},
	GeneralUnauthorized: {
		HTTPCode:    http.StatusUnauthorized,
		ServiceCode: GeneralUnauthorized,
		Message:     "Unauthorized",
	},
	GeneralForbidden: {
		HTTPCode:    http.StatusForbidden,
		ServiceCode: GeneralForbidden,
		Message:     "Forbidden",
	},
	ErrCodeAccommodationNotFound: {
		HTTPCode:    http.StatusNotFound,
		ServiceCode: ErrCodeAccommodationNotFound,
		Message:     AccommodationNotFoundMessage,
	},
}

// GetErrorResponse get error response from code
func GetErrorResponse(code int) ErrorResponse {
	if val, ok := errorResponseMap[code]; ok {
		return val
	}

	// default response
	return ErrorResponse{
		HTTPCode:    http.StatusInternalServerError,
		ServiceCode: code,
		Message:     http.StatusText(http.StatusInternalServerError),
	}
}
