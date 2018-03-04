package pro.vylgin.radiot.ui.allepisodes.sort

enum class SortType(val title: String) {
    ASC("По возрастанию"),
    DESC("По убыванию");

    override fun toString(): String {
        return title
    }
}