class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    private val reviewId = ArrayList()
    private var context: Context? = null

    val itemCount: Int
        @Override
        get() = reviewId.size()

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ReviewViewHolder {
        context = parent.getContext()
        val layoutIdForListItem = R.layout.rewiew_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)
        return ReviewViewHolder(view)
    }

    @Override
    fun onBindViewHolder(@NonNull holder: ReviewViewHolder, position: Int) {
        val currentReview = reviewId.get(position)
        val authorName = currentReview.getAuthorName()
        val relativeTimeDescription = currentReview.getRelativeTimeDescription()
        val authorRating = currentReview.getRating()
        val reviewText = currentReview.getText()
        holder.reviewAuthor.setText(authorName)
        holder.reviewTime.setText(String.valueOf(relativeTimeDescription))
        holder.review.setText(reviewText)
        holder.ratingBar.setRating(authorRating)
        // Content Description
        holder.ratingBar
                .setContentDescription(context!!.getString(R.string.the_rating_is_cd) + authorRating)
        holder.reviewAuthor
                .setContentDescription(context!!.getString(R.string.the_name_is_cd) + authorName)
        holder.review
                .setContentDescription(context!!.getString(R.string.the_revies_is_cd) + reviewText)
        holder.reviewTime.setContentDescription(
                context!!.getString(R.string.review_time_cd) + relativeTimeDescription)
    }

    fun addAll(reviews: List<Review>) {
        if (reviewId != null) {
            reviewId.clear()
            reviewId.addAll(reviews)
            notifyDataSetChanged()
        }
    }

    internal class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ratingBar: RatingBar
        private val review: TextView
        private val reviewTime: TextView
        private val reviewAuthor: TextView

        init {
            review = view.findViewById(R.id.review_place_id)
            reviewAuthor = view.findViewById(R.id.review_author)
            reviewTime = view.findViewById(R.id.review_time)
            ratingBar = view.findViewById(R.id.star_rating)
        }
    }

    companion object {

        private val LOG_TAG = ReviewAdapter::class.java!!.getSimpleName()
    }
}
