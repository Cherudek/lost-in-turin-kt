
class BarsAdapter(private val clickHandler: AdapterOnClickHandler, private val apiKey: String) : RecyclerView.Adapter<BarsAdapter.BarsViewHolder>() {
    private val barPlaceId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = barPlaceId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BarsViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.bars_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return BarsViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: BarsViewHolder, position: Int) {
        val currentPlaceId = barPlaceId.get(position)
        val photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference()
        val address = currentPlaceId.getVicinity()
        val name = currentPlaceId.getName()
        val rating = Optional.ofNullable(currentPlaceId.getRating())
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView)
        holder.name.setText(name)
        holder.address.setText(address)
        holder.imageView.setContentDescription(context!!.getString(R.string.the_image_view_cd) + name)
        holder.address.setContentDescription(context!!.getString(R.string.the_address_is_cd) + address)
        holder.name.setContentDescription(context!!.getString(R.string.the_name_is_cd) + name)
        rating.ifPresent({ aDouble -> holder.ratingBar.setRating(aDouble.floatValue()) })
    }

    fun addAll(result: List<Result>) {
        if (barPlaceId != null) {
            barPlaceId.clear()
            barPlaceId.addAll(result)
        }
        notifyDataSetChanged()
    }

    inner class BarsViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageView: ImageView
        private val name: TextView
        private val address: TextView
        private val ratingBar: RatingBar

        init {
            imageView = itemView.findViewById(R.id.bars_photo_place_id)
            name = itemView.findViewById(R.id.bars_place_name)
            address = itemView.findViewById(R.id.bars_place_address)
            ratingBar = itemView.findViewById(R.id.ratingBar)
            itemView.setOnClickListener(???({ this.onClick(it) }))
        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val result = barPlaceId.get(adapterPosition)
            val placeId = result.getPlaceId()
            Log.d(LOG_TAG, "The Place id clicked is$placeId")
            clickHandler.onClick(result)
        }
    }

    companion object {

        private val LOG_TAG = BarsAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
