package port

import "gorm.io/gorm"

type IDatabaseTransactionPort interface {
	StartTransaction() *gorm.DB
	Commit(txDB *gorm.DB) error
	Rollback(txDB *gorm.DB) error
}
