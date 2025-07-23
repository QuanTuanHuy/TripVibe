package adapter

import (
	"memo_service/src/core/port"

	"gorm.io/gorm"
)

type DBTransactionAdapter struct {
	db *gorm.DB
}

func (d *DBTransactionAdapter) CommitTransaction(tx *gorm.DB) error {
	return tx.Commit().Error
}

func (d *DBTransactionAdapter) RollbackTransaction(tx *gorm.DB) error {
	return tx.Rollback().Error
}

func (d *DBTransactionAdapter) StartTransaction() *gorm.DB {
	return d.db.Begin()
}

func NewDBTransactionAdapter(db *gorm.DB) port.IDBTransactionPort {
	return &DBTransactionAdapter{
		db: db,
	}
}
