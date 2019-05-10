class PhotoAdapter(private val apiKey: String, private val clickHandler: OnFragmentInteractionListener) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private val photoId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = photoId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PhotoAdapter.PhotoViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.photo_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return PhotoViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: PhotoAdapter.PhotoViewHolder, position: Int) {
        val currentPhoto = photoId.get(position)
        val photoReference = currentPhoto.getPhotoReference()
        val photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView)
        holder.imageView.setContentDescription(context!!.getString(R.string.photo_gallery_detail_view))
    }

    fun addAll(photos: List<Photo>) {
        if (photoId != null)
            photoId.clear()
        photoId.addAll(photos)
        notifyDataSetChanged()
    }

    internal inner class PhotoViewHolder private constructor(imageView: View) : RecyclerView.ViewHolder(imageView), View.OnClickListener {
        private val imageView: ImageView

        init {
            this.imageView = imageView.findViewById(R.id.photo_place_id)
            itemView.setOnClickListener(???({ this.onClick(it) }))

        }

        @Override
        fun onClick(v: View) {
            val adapterPosition = getAdapterPosition()
            val photo = photoId.get(adapterPosition)
            clickHandler.onFragmentInteraction(photo)
        }
    }

    companion object {

        private val LOG_TAG = PhotoAdapter::class.java!!.getSimpleName()
        private val PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?"
    }
}
