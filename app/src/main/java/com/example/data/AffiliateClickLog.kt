package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affiliate_clicks")
data class AffiliateClickLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val carId: Int,
    val modelName: String,
    val carPrice: String,
    val affiliateUrl: String,
    val affiliateId: String = "AFF_12345",
    val commissionEarned: Double, // Calculated 1.5% of car MSRP
    val timestamp: Long = System.currentTimeMillis()
)
