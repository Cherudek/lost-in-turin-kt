data class OpeningHours {

    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null
    @SerializedName("periods")
    @Expose
    var periods: List<Period>? = null
    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<String>? = null

}