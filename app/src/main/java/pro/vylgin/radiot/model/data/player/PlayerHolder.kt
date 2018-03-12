package pro.vylgin.radiot.model.data.player

interface PlayerHolder {
    var lastEpisodeNumber: Int
    var lastEpisodePositionInSeconds: Int
    var lastEpisodePositionTextFormatted: String
    var lastEpisodeDuration: Int
    var lastEpisodeDurationTextFormatted: String
    var lastEpisodeTimeLabelPosition: Int
    var lastEpisodeTimeLabelTopic: String
}