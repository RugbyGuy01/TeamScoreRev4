package com.golfpvcc.teamscore_rev4.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.golfpvcc.teamscore_rev4.database.converters.DataConverter
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.dao.EmailDao
import com.golfpvcc.teamscore_rev4.database.dao.JunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerJunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PointsDao
import com.golfpvcc.teamscore_rev4.database.dao.ScoreCardDao
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.database.model.EmailRecord
import com.golfpvcc.teamscore_rev4.database.model.JunkRecord
import com.golfpvcc.teamscore_rev4.database.model.PlayerJunkRecord
import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord


@TypeConverters(value = [DataConverter::class])


@Database(
    entities = [CourseRecord::class,
        ScoreCardRecord::class,
        PlayerRecord::class,
        PointsRecord::class,
        JunkRecord::class,
        EmailRecord::class,
        PlayerJunkRecord::class],
    version = 5,
    exportSchema = false
)

abstract class TeamScoreDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun playerDao(): PlayerDao
    abstract fun scoreCardDao(): ScoreCardDao
    abstract fun pointsDao(): PointsDao
    abstract fun junkDao(): JunkDao
    abstract fun emailDao(): EmailDao
    abstract fun playerJunkDao(): PlayerJunkDao
}