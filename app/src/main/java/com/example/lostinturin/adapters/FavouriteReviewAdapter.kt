class FavouriteReviewAdapter : RecyclerView.Adapter<FavouriteReviewAdapter.FavouriteReviewViewHold>() {
    private val mReviewId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = mReviewId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): FavouriteReviewViewHold {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.favourite_review_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return FavouriteReviewViewHold(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: FavouriteReviewViewHold, position: Int) {
        val currentReview = mReviewId.get(position)
        val authorName = currentReview.getAuthorName()
        val relativeTimeDescription = currentReview.getRelativeTimeDescription()
        val authorRating = currentReview.getRating()
        val reviewText = currentReview.getText()
        holder.tvReviewAuthor.setText(authorName)
        holder.tvReviewTime.setText(String.valueOf(relativeTimeDescription))
        holder.tvReview.setText(reviewText)
        holder.ratingBar.setRating(authorRating)
        holder.tvReviewAuthor
                .setContentDescription(context!!.getString(R.string.the_reviewer_name_cd) + authorName)
        holder.tvReview
                .setContentDescription(context!!.getString(R.string.the_revies_is_cd) + reviewText)
        holder.ratingBar
                .setContentDescription(context!!.getString(R.string.the_rating_is_cd) + authorRating)
    }

    fun addAll(reviews: List<Review>) {
        if (mReviewId != null) {
            mReviewId.clear()
            mReviewId.addAll(reviews)
            notifyDataSetChanged()
        }
    }

    internal class FavouriteReviewViewHold private constructor(view: View) : RecyclerView.ViewHolder(view) {

        private val tvReview: TextView
        private val tvReviewTime: TextView
        private val tvReviewAuthor: TextView
        private val ratingBar: RatingBar

        init {
            tvReview = view.findViewById(R.id.favourite_review_place_id)
            tvReviewAuthor = view.findViewById(R.id.favourite_review_author)
            tvReviewTime = view.findViewById(R.id.favourite_review_time)
            ratingBar = view.findViewById(R.id.favourite_star_rating)
        }
    }

    companion object {
        private val LOG_TAG = FavouriteReviewAdapter::class.java!!.getSimpleName()
    }
}
