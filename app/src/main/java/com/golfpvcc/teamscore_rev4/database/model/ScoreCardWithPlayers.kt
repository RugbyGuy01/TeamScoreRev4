package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ScoreCardWithPlayers(
    @Embedded
    val scoreCardRecord: ScoreCardRecord,

    @Relation(
        parentColumn = "scoreCardRecId",   // ties the score card record
        entityColumn = "scoreCardRecFk"    //Link to score card
    )
    val playerRecords: List<PlayerRecord>
)
