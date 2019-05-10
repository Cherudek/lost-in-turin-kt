class SightsAdapter(private val clickHandler: AdapterOnClickHandler, private val apiKey: String) : RecyclerView.Adapter<SightsAdapter.SightsViewHolder>() {
    private val sightsPlaceId = ArrayList()
    private var context: Context? = null


    val itemCount: Int
        @Override
        get() = sightsPlaceId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): SightsViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.sights_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return SightsViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: SightsViewHolder, position: Int) {
        val currentPlaceId = sightsPlaceId.get(position)
        val photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference()
        val address = currentPlaceId.getVicinity()
        val name = currentPlaceId.getName()
        val rating = currentPlaceId.getRating()
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.sightImage)
        holder.sightName.setText(name)
        holder.sightAddress.setText(address)
        holder.sightImage.setContentDescription(context!!.getString(R.string.the_image_view_cd) + name)
        holder.sightAddress
                .setContentDescription(context!!.getString(R.string.the_address_is_cd) + address)
        holder.sightName.setContentDescription(context!!.getString(R.string.the_name_is_cd) + name)
        holder.ratingBar.setRating(rating.floatValue())
    }

    fun addAll(result: List<Result>) {
        if (sightsPlaceId != null) {
            sightsPlaceId.clear()
            sightsPlaceId.addAll(result)
        }
        notifyDataSetChanged()
    }

    inner class SightsViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val sightImage: ImageView
        private val sightName: TextView
        private val sightAddress: TextView
        private val ratingBar: RatingBar


        init {
            sightImage = itemView.findViewById(R.id.sights_photo_place_id)
            sightName = itemView.findViewById(R.id.sights_place_name)
            sightAddress = itemView.findViewById(R.id.sights_place_address)
            ratingBar = itemView.findViewById(R.id.ratingSights)
            itemView.setOnClickListener(???({ this.onClick(it) }))
        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val result = sightsPlaceId.get(adapterPosition)
            val placeId = result.getPlaceId()
            Log.i(LOG_TAG, "The Place id clicked is$placeId")
            clickHandler.onClick(result)
        }
    }

    companion object {

        private val LOG_TAG = FavouritesAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
