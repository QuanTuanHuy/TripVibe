package adapter

import (
	"errors"

	"gorm.io/gorm"
)

type base struct {
	db *gorm.DB
}

func (b base) HandleError(err error) error {
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return errors.New("ResourceNotFound")
	}
	return err
}

func (b base) BeginTransaction() *gorm.DB {
	tx := b.db.Begin()
	return tx
}

func (b base) CommitTransaction(tx *gorm.DB) error {
	return tx.Commit().Error
}

func (b base) RollbackTransaction(tx *gorm.DB) error {
	return tx.Rollback().Error
}
