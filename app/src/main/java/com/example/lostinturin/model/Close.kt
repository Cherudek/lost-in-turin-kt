data class Close {

    @SerializedName("day")
    @Expose
    var day: Int? = null
    @SerializedName("time")
    @Expose
    var time: String? = null
}