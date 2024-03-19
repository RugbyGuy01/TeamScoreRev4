package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ScoreCardWithPlayers(
    @Embedded
    val scoreCardRecord: ScoreCardRecord,

    @Relation(
        parentColumn = "scoreCardRec_Id",   // ties the score card record
        entityColumn = "mId"                // to player record
    )

    val playerRecords: List<PlayerRecord>
)
