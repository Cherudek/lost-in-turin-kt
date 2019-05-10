class FavouritePhotoAdapter(private val apiKey: String) : RecyclerView.Adapter<FavouritePhotoAdapter.FavouritePhotoViewHolder>() {
    private val photoId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = photoId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): FavouritePhotoAdapter.FavouritePhotoViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.photo_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return FavouritePhotoAdapter.FavouritePhotoViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: FavouritePhotoViewHolder, position: Int) {
        val currentPhoto = photoId.get(position)
        val photoReference = currentPhoto.getPhotoReference()
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView)
        Log.i(LOG_TAG, "Photo Adapter GalleryUrl $photoUrl")
        holder.imageView.setContentDescription(context!!.getString(R.string.favourite_place_image))

    }

    fun addAll(photos: List<Photo>) {
        if (photoId != null) {
            photoId.clear()
            photoId.addAll(photos)
        }
        notifyDataSetChanged()
    }

    internal class FavouritePhotoViewHolder private constructor(imageView: View) : RecyclerView.ViewHolder(imageView) {
        private val imageView: ImageView
        init {
            this.imageView = imageView.findViewById(R.id.photo_place_id)
        }
    }

    companion object {
        private val LOG_TAG = PhotoAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
