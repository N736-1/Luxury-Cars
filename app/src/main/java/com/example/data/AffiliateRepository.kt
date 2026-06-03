package com.example.data

import kotlinx.coroutines.flow.Flow

class AffiliateRepository(
    private val garageDao: GarageDao,
    private val clickDao: ClickDao
) {
    // Expose flows for reactive subscription in ViewModels
    val garageItems: Flow<List<GarageItem>> = garageDao.getGarageItems()
    val clicksLog: Flow<List<AffiliateClickLog>> = clickDao.getClicks()

    // Retrieve full catalog list
    fun getCatalog(): List<Car> = CarCatalog.items

    suspend fun addToGarage(car: Car) {
        val garageItem = GarageItem(
            id = car.id,
            brand = car.brand,
            model = car.model,
            year = car.year,
            condition = car.condition,
            price = car.price,
            numericPrice = car.numericPrice,
            imageUrl = car.imageUrl
        )
        garageDao.addToGarage(garageItem)
    }

    suspend fun removeFromGarage(carId: Int) {
        garageDao.removeFromGarage(carId)
    }

    suspend fun clearGarage() {
        garageDao.clearGarage()
    }

    suspend fun logAffiliateClick(car: Car, affiliateId: String = "AFF_12345") {
        // Calculate a 1.5% affiliate commission for tracking
        val commission = car.numericPrice * 0.015
        val log = AffiliateClickLog(
            carId = car.id,
            modelName = car.fullName,
            carPrice = car.price,
            affiliateUrl = car.affiliateUrl,
            affiliateId = affiliateId,
            commissionEarned = commission
        )
        clickDao.logClick(log)
    }

    suspend fun logBatchAffiliateCheckout(items: List<GarageItem>, affiliateId: String = "AFF_12345") {
        for (item in items) {
            val commission = item.numericPrice * 0.015
            val log = AffiliateClickLog(
                carId = item.id,
                modelName = item.fullName,
                carPrice = item.price,
                affiliateUrl = "https://www.mercedesbenzgreenwich.com/inventory/?aff_id=$affiliateId",
                affiliateId = affiliateId,
                commissionEarned = commission
            )
            clickDao.logClick(log)
        }
    }

    suspend fun clearClicksLog() {
        clickDao.clearClicksLog()
    }
}
