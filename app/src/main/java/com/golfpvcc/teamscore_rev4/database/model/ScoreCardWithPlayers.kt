package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ScoreCardWithPlayers(
    @Embedded
    val scoreCardRecord: ScoreCardRecord,

    @Relation(
        parentColumn = "mScoreCardRecId",   // ties the score card record
        entityColumn = "mScoreCardRecFk"    //Link to score card
    )
    val playerRecords: List<PlayerRecord>
)
