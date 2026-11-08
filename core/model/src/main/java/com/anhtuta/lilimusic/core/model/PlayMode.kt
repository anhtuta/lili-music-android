package com.anhtuta.lilimusic.core.model

/**
 * Web `playType` values. Mutually exclusive: shuffle and repeat-one each toggle back to sequence.
 */
enum class PlayMode(
    val storageValue: String,
) {
    SEQUENCE("SEQUENCE"),
    SHUFFLE("SHUFFLE"),
    REPEAT_ONE("REPEAT_ONE"),
    ;

    fun toggleShuffle(): PlayMode = if (this == SHUFFLE) SEQUENCE else SHUFFLE

    fun toggleRepeatOne(): PlayMode = if (this == REPEAT_ONE) SEQUENCE else REPEAT_ONE

    /** Web `playTypeList`: Sequence → Shuffle → Repeat one → Sequence. */
    fun cycle(): PlayMode =
        when (this) {
            SEQUENCE -> SHUFFLE
            SHUFFLE -> REPEAT_ONE
            REPEAT_ONE -> SEQUENCE
        }

    companion object {
        fun fromStorage(value: String?): PlayMode = entries.find { it.storageValue == value } ?: SEQUENCE
    }
}
