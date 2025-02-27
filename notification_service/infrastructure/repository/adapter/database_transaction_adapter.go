package adapter

import (
	"gorm.io/gorm"
	"notification_service/core/port"
)

type DatabaseTransactionAdapter struct {
	base
}

func (d *DatabaseTransactionAdapter) StartTransaction() *gorm.DB {
	return d.base.BeginTransaction()
}

func (d *DatabaseTransactionAdapter) Commit(txDB *gorm.DB) error {
	return d.base.CommitTransaction(txDB)
}

func (d *DatabaseTransactionAdapter) Rollback(txDB *gorm.DB) error {
	return d.base.RollbackTransaction(txDB)
}

func NewDatabaseTransactionAdapter(db *gorm.DB) port.IDatabaseTransactionPort {
	return &DatabaseTransactionAdapter{
		base: base{db},
	}
}
