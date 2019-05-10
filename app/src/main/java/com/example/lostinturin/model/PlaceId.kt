data class PlaceId {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("result")
    @Expose
    var result: Result? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

}