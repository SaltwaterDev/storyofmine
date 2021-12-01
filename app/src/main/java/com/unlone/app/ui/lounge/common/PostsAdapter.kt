package com.unlone.app.ui.lounge.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.instance.Post
import com.unlone.app.utils.PostDiffUtil
import com.unlone.app.utils.dpConvertPx
import com.unlone.app.utils.getImageHorizontalMargin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target

class PostsAdapter(var context: Context) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    private var postList = emptyList<Post>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: CardView = itemView.findViewById(R.id.card_view)
        var title: TextView = itemView.findViewById(R.id.textView_title)
        var journal: TextView = itemView.findViewById(R.id.textView_journal)
        val imageCover: ImageView = itemView.findViewById(R.id.imageCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (title, image_path, journal, _, _, _, _, _, _, pid) = postList[position]

        // set title
        holder.title.text = title

        // set text
        holder.journal.text = journal

        // control what to display on post view
        if (image_path.isNotEmpty()) {
            Log.d("TAG", "image path: $image_path")



            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    holder.imageCover.visibility = View.VISIBLE
                    holder.title.visibility = View.VISIBLE
                    holder.journal.visibility = View.GONE
                    holder.title.gravity = Gravity.CENTER

                    //get measured image size
                    val imageWidth = bitmap.width
                    val imageHeight = bitmap.height
                    holder.imageCover.setImageBitmap(bitmap)
                    Log.d("Bitmap Dimensions: ", imageWidth.toString() + "x" + imageHeight)
                    var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.gravity = Gravity.CENTER
                    val imageHorizontalMargin = getImageHorizontalMargin(imageWidth.toFloat() / imageHeight, context) // in px
                    val imageVerticalMargin = dpConvertPx(60, context)
                    params.setMargins(imageHorizontalMargin, imageVerticalMargin, imageHorizontalMargin, 0)
                    holder.imageCover.layoutParams = params

                    // reset bottom margin
                    val textHeight = getHeight(context, holder.title)
                    val textWhitespace = (imageVerticalMargin + imageHeight) / 3 - textHeight
                    Log.d("whitespace", textWhitespace.toString())
                    Log.d("whitespace_text", textHeight.toString())
                    val textTopMargin = (textWhitespace / (1 + 1.5)).toInt()
                    val textBottomMargin = (textWhitespace * 1.5 / (1 + 1.5)).toInt()
                    params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(dpConvertPx(18, context), textTopMargin, dpConvertPx(18, context), textBottomMargin)
                    holder.title.layoutParams = params
                    holder.title.visibility = View.VISIBLE
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                // load image and resize it
                // action wil be done when load the image

            }
            // set image
            holder.imageCover.tag = target
            Picasso.get().load(image_path).into(target)
        } else {
            holder.journal.visibility = View.VISIBLE
            holder.imageCover.visibility = View.GONE
            val params: LinearLayout.LayoutParams
            if (title.isNotEmpty()) {
                holder.title.visibility = View.VISIBLE
                // journal top margin
                params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(dpConvertPx(16, context), dpConvertPx(21, context), dpConvertPx(16, context), dpConvertPx(41, context))
            } else {
                holder.title.visibility = View.GONE
                // journal top margin
                params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(dpConvertPx(16, context), dpConvertPx(32, context), dpConvertPx(16, context), dpConvertPx(41, context))
                holder.journal.maxLines = 6
            }
            holder.journal.layoutParams = params
        }
        holder.card.setOnClickListener {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("postId", pid)
            context.startActivity(intent)
        }
    }

    fun setPostList(postList: List<Post>) {
        Log.d("TAG", "setPostList oldPostList: ${this.postList}")
        Log.d("TAG", "setPostList newPostList: $postList")
        val diffUtil = PostDiffUtil(this.postList, postList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        this.postList = postList
        diffResults.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    companion object {
        fun getHeight(context: Context, textView: TextView): Int {
            val displayMetrics = context.resources.displayMetrics
            // val deviceHeight = displayMetrics.heightPixels
            val deviceWidth = displayMetrics.widthPixels
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            textView.measure(widthMeasureSpec, heightMeasureSpec)
            return textView.measuredHeight
        }
    }

}