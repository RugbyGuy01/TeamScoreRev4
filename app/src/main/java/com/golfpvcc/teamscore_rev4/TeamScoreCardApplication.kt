package com.golfpvcc.teamscore_rev4
/*
This function is called from the application Android manifest.xml
 */
import android.app.Application
import androidx.room.Room
import com.golfpvcc.teamscore_rev4.utils.Constants.DATABASE_NAME
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerDao
import com.golfpvcc.teamscore_rev4.database.dao.ScoreCardDao
import com.golfpvcc.teamscore_rev4.database.room.TeamScoreDatabase

class TeamScoreCardApp : Application() {

    private var db: TeamScoreDatabase? = null

    init {
        instance = this
    }

    private fun getDb(): TeamScoreDatabase {
        if (db != null) {
            return db!!
        } else {
            db = Room.databaseBuilder(
                instance!!.applicationContext,
                TeamScoreDatabase::class.java, DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()

            return db!!
        }
    }

    companion object {
        private var instance: TeamScoreCardApp? = null

        fun getCourseDao(): CourseDao {
            return instance!!.getDb().courseDao()
        }

        fun getScoreCardDao(): ScoreCardDao {
            return instance!!.getDb().scoreCardDao()
        }

        fun getPlayerDao(): PlayerDao {
            return instance!!.getDb().playerDao()
        }

    }

}
