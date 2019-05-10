class FoodAdapter(private val mClickHandler: AdapterOnClickHandler, private val apiKey: String) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private val foodPlaceId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = foodPlaceId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): FoodViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.food_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return FoodViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: FoodViewHolder, position: Int) {
        val currentPlaceId = foodPlaceId.get(position)
        val photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference()
        val address = currentPlaceId.getVicinity()
        val name = currentPlaceId.getName()
        val rating = Optional.ofNullable(currentPlaceId.getRating())
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.image)
        holder.name.setText(name)
        holder.address.setText(address)
        holder.image.setContentDescription(context!!.getString(R.string.the_image_view_cd) + name)
        holder.address.setContentDescription(context!!.getString(R.string.the_address_is_cd) + address)
        holder.name.setContentDescription(context!!.getString(R.string.the_name_is_cd) + name)
        rating.ifPresent({ aDouble -> holder.ratingBar.setRating(aDouble.floatValue()) })
    }

    fun addAll(result: List<Result>) {
        if (foodPlaceId != null) {
            foodPlaceId.clear()
        }
        assert(foodPlaceId != null)
        foodPlaceId.addAll(result)
        notifyDataSetChanged()
    }

    interface AdapterOnClickHandler {
        fun onClick(result: Result)
    }

    inner class FoodViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val image: ImageView
        private val name: TextView
        private val address: TextView
        private val ratingBar: RatingBar


        init {
            image = itemView.findViewById(R.id.food_photo_place_id)
            name = itemView.findViewById(R.id.food_place_name)
            address = itemView.findViewById(R.id.food_place_address)
            ratingBar = itemView.findViewById(R.id.ratingFood)
            itemView.setOnClickListener(???({ this.onClick(it) }))
        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val result = foodPlaceId.get(adapterPosition)
            val placeId = result.getPlaceId()
            Log.i(LOG_TAG, "The Place id clicked is$placeId")
            mClickHandler.onClick(result)
        }
    }

    companion object {

        private val LOG_TAG = FoodAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
