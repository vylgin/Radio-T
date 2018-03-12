package pro.vylgin.radiot.model.data.player

import pro.vylgin.radiot.entity.TimeLabel

data class SeekModel(
        var durationTextFormatted: String,
        var durationInSeconds: Int,
        var currentPositionTextFormatted: String,
        var currentPositionInSeconds: Int,
        var currentTimeLabel: TimeLabel?,
        var currentTimeLabelPosition: Int,
        var buffer: Int
)