package com.golfpvcc.teamscore_rev4
/*
This function is called from the application Android manifest.xml
 */
import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.golfpvcc.teamscore_rev4.utils.DATABASE_NAME
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.dao.EmailDao
import com.golfpvcc.teamscore_rev4.database.dao.JunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerJunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PointsDao
import com.golfpvcc.teamscore_rev4.database.dao.ScoreCardDao
import com.golfpvcc.teamscore_rev4.database.room.TeamScoreDatabase
import java.io.File
import java.io.IOException

class TeamScoreCardApp : Application() {

    private var db: TeamScoreDatabase? = null

    init {
        instance = this
    }

    fun closeDB(){
        db = null
    }


    private fun getDb(): TeamScoreDatabase {
        if (db != null) {
            return db!!
        } else {
            db = Room.databaseBuilder(
                instance!!.applicationContext,
                TeamScoreDatabase::class.java, DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .build()

            return db!!
        }
    }

    companion object {
        private var instance: TeamScoreCardApp? = null
        fun closeTeamScoreDatabase() {
            instance!!.getDb().close()
            instance!!.closeDB()
        }

        fun getRoomDatabase():TeamScoreDatabase{
            return instance!!.getDb()
        }
        fun getCourseDao(): CourseDao {
            return instance!!.getDb().courseDao()
        }

        fun getScoreCardDao(): ScoreCardDao {
            return instance!!.getDb().scoreCardDao()
        }

        fun getPlayerDao(): PlayerDao {
            return instance!!.getDb().playerDao()
        }

        fun getPointsDao(): PointsDao {
            return instance!!.getDb().pointsDao()
        }
        fun getJunkDao(): JunkDao {
            return instance!!.getDb().junkDao()
        }
        fun getEmailDao(): EmailDao {
            return instance!!.getDb().emailDao()
        }
        fun getPlayerJunkDao(): PlayerJunkDao {
            return instance!!.getDb().playerJunkDao()
        }
    }
}
