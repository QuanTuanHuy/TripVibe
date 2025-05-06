package usecase

//
//import (
//	"booking_service/core/domain/dto/request"
//	"booking_service/core/domain/entity"
//	"booking_service/core/port"
//	"context"
//	"encoding/json"
//	"errors"
//	"github.com/golibs-starter/golib/log"
//	"time"
//)
//
//// IOneClickBookingUseCase định nghĩa use case cho đặt phòng nhanh với 1 click
//type IOneClickBookingUseCase interface {
//	// OneClickBooking thực hiện đặt phòng nhanh từ cấu hình đã lưu
//	OneClickBooking(ctx context.Context, userID int64, req *request.OneClickBookingDto) (*entity.BookingEntity, error)
//}
//
//// OneClickBookingUseCase triển khai use case cho đặt phòng nhanh với 1 click
//type OneClickBookingUseCase struct {
//	quickBookingPort     port.IQuickBookingPort
//	bookingPort          port.IBookingPort
//	bookingUnitPort      port.IBookingUnitPort
//	bookingPromotionPort port.IBookingPromotionPort
//	accommodationPort    port.IAccommodationPort
//	unitPort             port.IUnitPort
//	promotionPort        port.IPromotionPort
//	profilePort          port.IProfilePort
//	dbTransactionUseCase IDatabaseTransactionUseCase
//}
//
//// NewOneClickBookingUseCase tạo instance mới cho OneClickBookingUseCase
//func NewOneClickBookingUseCase(
//	quickBookingPort port.IQuickBookingPort,
//	bookingPort port.IBookingPort,
//	bookingUnitPort port.IBookingUnitPort,
//	bookingPromotionPort port.IBookingPromotionPort,
//	accommodationPort port.IAccommodationPort,
//	unitPort port.IUnitPort,
//	promotionPort port.IPromotionPort,
//	profilePort port.IProfilePort,
//	trx port.IDbTransaction,
//) IOneClickBookingUseCase {
//	return &OneClickBookingUseCase{
//		quickBookingPort:     quickBookingPort,
//		bookingPort:          bookingPort,
//		bookingUnitPort:      bookingUnitPort,
//		bookingPromotionPort: bookingPromotionPort,
//		accommodationPort:    accommodationPort,
//		unitPort:             unitPort,
//		promotionPort:        promotionPort,
//		profilePort:          profilePort,
//		dbTransactionUseCase: trx,
//	}
//}
//
//// OneClickBooking triển khai use case đặt phòng nhanh với 1 click
//func (o OneClickBookingUseCase) OneClickBooking(ctx context.Context, userID int64, req *request.OneClickBookingDto) (*entity.BookingEntity, error) {
//	// Get quick booking configuration
//	quickBooking, err := o.quickBookingPort.GetQuickBooking(ctx, req.QuickBookingID, userID)
//	if err != nil {
//		log.Error(ctx, "Error getting quick booking", err)
//		return nil, err
//	}
//
//	// Check if stay dates are valid
//	if req.StayFrom >= req.StayTo {
//		return nil, errors.New("invalid stay dates: check-in must be before check-out")
//	}
//
//	// Get accommodation information
//	accommodation, err := o.accommodationPort.GetAccommodationByID(ctx, quickBooking.AccommodationID)
//	if err != nil {
//		log.Error(ctx, "Error getting accommodation", err)
//		return nil, err
//	}
//
//	// Get user profile for booking
//	profile, err := o.profilePort.GetProfile(ctx, userID)
//	if err != nil {
//		log.Error(ctx, "Error getting user profile", err)
//		return nil, err
//	}
//
//	// Get units information
//	var unitIDs []int64
//	if len(req.UnitIDs) > 0 {
//		unitIDs = req.UnitIDs
//	} else {
//		// Use preferred units from quick booking configuration
//		if err := json.Unmarshal([]byte(quickBooking.PreferredUnitIDs), &unitIDs); err != nil {
//			log.Error(ctx, "Error unmarshalling preferred unit IDs", err)
//			return nil, err
//		}
//	}
//
//	units, err := o.unitPort.GetUnitsByIDs(ctx, unitIDs)
//	if err != nil {
//		log.Error(ctx, "Error getting units", err)
//		return nil, err
//	}
//
//	// Check if all requested units are available
//	for _, unit := range units {
//		isAvailable, err := o.unitPort.CheckUnitAvailable(ctx, unit.ID, req.StayFrom, req.StayTo)
//		if err != nil {
//			log.Error(ctx, "Error checking unit availability", err)
//			return nil, err
//		}
//		if !isAvailable {
//			return nil, errors.New("one or more units are not available for the selected dates")
//		}
//	}
//
//	// Calculate booking amount
//	var totalAmount int64
//	bookingUnits := make([]*entity.BookingUnitEntity, 0, len(units))
//	for _, unit := range units {
//		// Get unit price for the stay period (simplified, in real world would consider dynamic pricing)
//		unitPrice, err := o.unitPort.GetUnitPrice(ctx, unit.ID, req.StayFrom, req.StayTo)
//		if err != nil {
//			log.Error(ctx, "Error getting unit price", err)
//			return nil, err
//		}
//
//		totalAmount += unitPrice
//
//		// Create booking unit entity
//		bookingUnit := &entity.BookingUnitEntity{
//			UnitID:   unit.ID,
//			FullName: unit.Name, // Simplified, would use profile name in real implementation
//			Email:    profile.Email,
//			Amount:   unitPrice,
//		}
//		bookingUnits = append(bookingUnits, bookingUnit)
//	}
//
//	// Apply promotions if specified
//	var finalAmount = totalAmount
//	var bookingPromotions []*entity.BookingPromotionEntity
//	if len(req.PromotionIDs) > 0 {
//		promotions, err := o.promotionPort.GetPromotionsByIDs(ctx, req.PromotionIDs)
//		if err != nil {
//			log.Error(ctx, "Error getting promotions", err)
//			return nil, err
//		}
//
//		for _, promotion := range promotions {
//			// Apply discount to total amount (simplified logic)
//			discount := (totalAmount * int64(promotion.DiscountPercentage)) / 100
//			finalAmount -= discount
//
//			// Create booking promotion entity
//			bookingPromotion := &entity.BookingPromotionEntity{
//				PromotionID:        promotion.ID,
//				PromotionName:      promotion.Name,
//				DiscountPercentage: promotion.DiscountPercentage,
//			}
//			bookingPromotions = append(bookingPromotions, bookingPromotion)
//		}
//	}
//
//	// Create booking entity
//	booking := &entity.BookingEntity{
//		UserID:             userID,
//		AccommodationID:    quickBooking.AccommodationID,
//		CurrencyID:         accommodation.CurrencyID, // Using accommodation's default currency
//		NumberOfAdult:      quickBooking.NumberOfAdult,
//		NumberOfChild:      quickBooking.NumberOfChild,
//		Note:               req.Note,
//		StayFrom:           req.StayFrom,
//		StayTo:             req.StayTo,
//		Status:             "PENDING", // Initial status
//		InvoiceAmount:      totalAmount,
//		FinalInvoiceAmount: finalAmount,
//		CreatedAt:          time.Now(),
//		UpdatedAt:          time.Now(),
//	}
//
//	// Start transaction
//	tx, err := o.dbTransactionUseCase.BeginTransactionFromContext(ctx)
//	if err != nil {
//		log.Error(ctx, "Error beginning transaction", err)
//		return nil, err
//	}
//	defer o.dbTransactionUseCase.RollbackTransactionFromContext(ctx, tx)
//
//	// Create booking
//	booking, err = o.bookingPort.CreateBooking(ctx, tx, booking)
//	if err != nil {
//		log.Error(ctx, "Error creating booking", err)
//		return nil, err
//	}
//
//	// Set booking ID for booking units and create them
//	for _, bu := range bookingUnits {
//		bu.BookingID = booking.ID
//	}
//	bookingUnits, err = o.bookingUnitPort.CreateBookingUnits(ctx, tx, bookingUnits)
//	if err != nil {
//		log.Error(ctx, "Error creating booking units", err)
//		return nil, err
//	}
//
//	// Set booking ID for booking promotions and create them if there are any
//	if len(bookingPromotions) > 0 {
//		for _, bp := range bookingPromotions {
//			bp.BookingID = booking.ID
//		}
//		bookingPromotions, err = o.bookingPromotionPort.CreateBookingPromotions(ctx, tx, bookingPromotions)
//		if err != nil {
//			log.Error(ctx, "Error creating booking promotions", err)
//			return nil, err
//		}
//	}
//
//	// Update last used timestamp for quick booking
//	err = o.quickBookingPort.UpdateLastUsed(ctx, tx, quickBooking.ID, userID)
//	if err != nil {
//		log.Error(ctx, "Error updating quick booking last used time", err)
//		// Continue even if this fails, it's not critical
//	}
//
//	// Commit transaction
//	if err = o.dbTransactionUseCase.CommitTransactionFromContext(ctx, tx); err != nil {
//		log.Error(ctx, "Error committing transaction", err)
//		return nil, err
//	}
//
//	// Return created booking
//	return booking, nil
//}
