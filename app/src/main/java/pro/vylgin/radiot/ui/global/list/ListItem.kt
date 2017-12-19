package pro.vylgin.radiot.ui.global.list

import pro.vylgin.radiot.entity.Entry


sealed class ListItem {
    class ProgressItem : ListItem()
    class PodcastItem(val entry: Entry) : ListItem()
    class PrepItem(val entry: Entry) : ListItem()
    class NewsItem(val entry: Entry) : ListItem()
    class InfoItem(val entry: Entry) : ListItem()
    class SpecialItem(val entry: Entry) : ListItem()
}
