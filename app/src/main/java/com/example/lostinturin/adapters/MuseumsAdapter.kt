class MuseumsAdapter(private val mClickHandler: AdapterOnClickHandler, private val apiKey: String) : RecyclerView.Adapter<MuseumsAdapter.ViewHolder>() {
    private val museumList = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = museumList.size()

    @Override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.museums_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return ViewHolder(view)
    }

    @Override
    fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentPlaceId = museumList.get(position)
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
        if (museumList != null) {
            museumList.clear()
            museumList.addAll(result)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val imageView: ImageView
        val name: TextView
        val address: TextView
        private val ratingBar: RatingBar


        init {
            imageView = view.findViewById(R.id.museums_photo_place_id)
            name = view.findViewById(R.id.museums_place_name)
            address = view.findViewById(R.id.museums_place_address)
            ratingBar = view.findViewById(R.id.ratingMuseums)
            view.setOnClickListener(???({ this.onClick(it) }))
        }

        @Override
        fun toString(): String {
            return super.toString() + " '" + address.getText() + "'"
        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val result = museumList.get(adapterPosition)
            val placeId = result.getPlaceId()
            Log.i(LOG_TAG, "The Place id clicked is$placeId")
            mClickHandler.onClick(result)
        }
    }

    companion object {

        private val LOG_TAG = MuseumsAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
