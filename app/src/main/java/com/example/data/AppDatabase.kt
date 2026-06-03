package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface GarageDao {
    @Query("SELECT * FROM garage_items ORDER BY addedAt DESC")
    fun getGarageItems(): Flow<List<GarageItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToGarage(item: GarageItem)

    @Query("DELETE FROM garage_items WHERE id = :id")
    suspend fun removeFromGarage(id: Int)

    @Query("DELETE FROM garage_items")
    suspend fun clearGarage()
}

@Dao
interface ClickDao {
    @Query("SELECT * FROM affiliate_clicks ORDER BY timestamp DESC")
    fun getClicks(): Flow<List<AffiliateClickLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logClick(click: AffiliateClickLog)

    @Query("DELETE FROM affiliate_clicks")
    suspend fun clearClicksLog()
}

@Database(entities = [GarageItem::class, AffiliateClickLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun garageDao(): GarageDao
    abstract fun clickDao(): ClickDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "luxury_auto_affiliates_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
