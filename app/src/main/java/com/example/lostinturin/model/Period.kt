data class Period {

    @SerializedName("close")
    @Expose
    var close: Close? = null
    @SerializedName("open")
    @Expose
    var open: Open? = null

}