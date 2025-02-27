package usecase

import (
	"gorm.io/gorm"
	"notification_service/core/port"
)

type IDatabaseTransactionUseCase interface {
	StartTransaction() *gorm.DB
	Commit(txDB *gorm.DB) error
	Rollback(txDB *gorm.DB) error
}
type DatabaseTransactionUseCase struct {
	databaseTransactionPort port.IDatabaseTransactionPort
}

func (d *DatabaseTransactionUseCase) StartTransaction() *gorm.DB {
	return d.databaseTransactionPort.StartTransaction()
}

func (d *DatabaseTransactionUseCase) Commit(txDB *gorm.DB) error {
	return d.databaseTransactionPort.Commit(txDB)
}

func (d *DatabaseTransactionUseCase) Rollback(txDB *gorm.DB) error {
	return d.databaseTransactionPort.Rollback(txDB)
}

func NewDatabaseTransactionUseCase(databaseTransactionPort port.IDatabaseTransactionPort) IDatabaseTransactionUseCase {
	return &DatabaseTransactionUseCase{
		databaseTransactionPort: databaseTransactionPort,
	}
}
