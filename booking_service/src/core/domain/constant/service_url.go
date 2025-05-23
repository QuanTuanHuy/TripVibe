package constant

const (
	NOTIFICATION_SERVICE = "notification"
	INVENTORY_SERVICE    = "inventory"
	PROMOTION_SERVICE    = "promotion"
)

const (
	NOTIFICATION_SERVICE_URL = "http://localhost:8082/notification_service"
	INVENTORY_SERVICE_URL    = "http://localhost:8093/inventory_service"
	PROMOTION_SERVICE_URL    = "http://localhost:8087/promotion_service"
)

const (
	SEND_NOTIFICATION_ENPOINT = "/api/public/v1/notifications"
	VERIFY_PROMOTION_ENPOINT  = "/api/public/v1/promotions/verify"
	LOCK_ROOM_FOR_BOOKING     = "/api/v1/inventories/lock"
	CONFIRM_BOOKING           = "/api/v1/inventories/confirm_booking"
	RELEASE_LOCK              = "/api/v1/inventories/release_lock/%s"
	CANCEL_BOOKING_INVENTORY  = "/api/v1/inventories/cancel_booking"
)
