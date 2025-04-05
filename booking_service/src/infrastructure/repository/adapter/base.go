package adapter

import (
	"errors"
	"gorm.io/gorm"
	"time"
)

type base struct {
	db *gorm.DB
}

func (b base) HandleError(err error) error {
	if errors.Is(err, gorm.ErrRecordNotFound) {
		//return exception.ResourceNotFound
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

func (b base) BeforeUpdate(tx *gorm.DB) (err error) {
	// if any fields changed
	if tx.Statement.Changed() {
		tx.Statement.SetColumn("UpdatedAt", time.Now())
	}
	return nil
}

func (b base) GetSkip(page, pageSize int64) int64 {
	return page * pageSize
}
