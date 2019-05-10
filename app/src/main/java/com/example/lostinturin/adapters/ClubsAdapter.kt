class ClubsAdapter(private val clickHandler: AdapterOnClickHandler, private val apiKey: String) : RecyclerView.Adapter<ClubsAdapter.ViewHolder>() {
    private val clubPlaceId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = clubPlaceId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.clubs_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return ViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {

        val currentPlaceId = clubPlaceId.get(position)
        val photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference()
        val address = currentPlaceId.getVicinity()
        val name = currentPlaceId.getName()
        val rating = currentPlaceId.getRating()
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView)
        holder.name.setText(name)
        holder.address.setText(address)
        holder.imageView.setContentDescription(context!!.getString(R.string.the_image_view_cd) + name)
        holder.address.setContentDescription(context!!.getString(R.string.the_address_is_cd) + address)
        holder.name.setContentDescription(context!!.getString(R.string.the_name_is_cd) + name)
        holder.ratingBar.setRating(rating.floatValue())
    }

    fun addAll(result: List<Result>) {
        if (clubPlaceId != null) {
            clubPlaceId.clear()
            clubPlaceId.addAll(result)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageView: ImageView
        private val name: TextView
        private val address: TextView
        private val ratingBar: RatingBar


        init {
            imageView = itemView.findViewById(R.id.clubs_photo_place_id)
            name = itemView.findViewById(R.id.club_place_name)
            address = itemView.findViewById(R.id.club_place_address)
            ratingBar = itemView.findViewById(R.id.ratingClub)
            itemView.setOnClickListener(???({ this.onClick(it) }))
        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val result = clubPlaceId.get(adapterPosition)
            val placeId = result.getPlaceId()
            Log.i(LOG_TAG, "The Place id clicked is$placeId")
            clickHandler.onClick(result)
        }
    }

    companion object {

        private val LOG_TAG = ClubsAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
