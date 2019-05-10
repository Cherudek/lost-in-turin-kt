class FavouritesAdapter(private val mClickHandler: FavouriteAdapterOnClickHandler, size: Int, private val apiKey: String) : RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder>() {
    private val favouritesPlaceId = ArrayList()
    private var context: Context? = null
    private var photoReference = ""

    val itemCount: Int
        @Override
        get() {
            Log.i(LOG_TAG, "getItemCount PlaceId Size = " + favouritesPlaceId.size())
            return favouritesPlaceId.size()
        }

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.favourite_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return FavouriteViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: FavouriteViewHolder, position: Int) {
        val currentPlaceId = favouritesPlaceId.get(position)
        if (currentPlaceId.getPhotos() != null) {
            photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference()
        }
        val address = currentPlaceId.getVicinity()
        val name = currentPlaceId.getName()
        val rating = currentPlaceId.getRating()
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.favouriteImage)

        ViewCompat.setTransitionName(holder.favouriteImage, name)

        holder.favouriteName.setText(name)
        holder.favouriteAddress.setText(address)
        holder.favouriteImage
                .setContentDescription(context!!.getString(R.string.the_image_view_cd) + name)
        holder.favouriteAddress
                .setContentDescription(context!!.getString(R.string.the_address_is_cd) + address)
        holder.favouriteName.setContentDescription(context!!.getString(R.string.the_name_is_cd) + name)
        holder.ratingBar.setRating(rating.floatValue())
    }

    fun addAll(result: List<Result>) {
        if (favouritesPlaceId != null) {
            favouritesPlaceId.clear()
            favouritesPlaceId.addAll(result)
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        favouritesPlaceId.remove(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Result, position: Int) {
        favouritesPlaceId.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }

    /**
     * The interface that receives onClick messages.
     */
    interface FavouriteAdapterOnClickHandler {
        fun onClick(result: Result, view: View)
    }

    inner class FavouriteViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val viewForeground: CardView
        private val favouriteImage: ImageView
        private val favouriteName: TextView
        private val favouriteAddress: TextView
        private val ratingBar: RatingBar


        init {
            favouriteImage = itemView.findViewById(R.id.favourite_photo_place_id)
            favouriteName = itemView.findViewById(R.id.favourite_place_name)
            favouriteAddress = itemView.findViewById(R.id.favourite_place_address)
            viewForeground = itemView.findViewById(R.id.favourite_card_view)
            ratingBar = itemView.findViewById(R.id.ratingFavourites)
            itemView.setOnClickListener(this)
        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val result = favouritesPlaceId.get(adapterPosition)
            val placeId = result.getPlaceId()
            Log.i(LOG_TAG, "The Place id clicked is$placeId")
            mClickHandler.onClick(result, v)
        }
    }

    companion object {

        private val LOG_TAG = FavouritesAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
