package port

import "context"

type ICachePort interface {
	SetToCache(ctx context.Context, key string, value interface{}, ttl int) error
	GetFromCache(ctx context.Context, key string) (interface{}, error)
	DeleteFromCache(ctx context.Context, key string) error
}
