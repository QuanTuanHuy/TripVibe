package port

import "gorm.io/gorm"

type IDBTransactionPort interface {
	StartTransaction() *gorm.DB
	CommitTransaction(tx *gorm.DB) error
	RollbackTransaction(tx *gorm.DB) error
}
