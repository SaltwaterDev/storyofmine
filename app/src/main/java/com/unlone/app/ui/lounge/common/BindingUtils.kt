package com.unlone.app.ui.lounge.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.unlone.app.R
import com.unlone.app.model.*
import com.unlone.app.utils.convertTimeStamp
import com.unlone.app.utils.dpConvertPx
import com.unlone.app.utils.getImageHorizontalMargin

@BindingAdapter("android:onClick")
fun MaterialCardView.setOnClick(item: Post) {
    setOnClickListener {
        val intent = Intent(context, PostDetailActivity::class.java)
        intent.putExtra("postId", item.pid)
        context.startActivity(intent)
    }
}


@BindingAdapter("journal")
fun TextView.setJournal(item: Post) {
    text = item.journal
}

fun getHeight(context: Context, textView: TextView): Int {
    val displayMetrics = context.resources.displayMetrics
    val deviceHeight = displayMetrics.heightPixels
    val deviceWidth = displayMetrics.widthPixels
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    textView.measure(widthMeasureSpec, heightMeasureSpec)
    return textView.measuredHeight
}

@BindingAdapter("postImage", "title")
fun ImageView.setPostImage(imagePath: String, title: TextView) {
    // TODO set the image space
    if (imagePath.isNotEmpty()) {
        Log.d("TAG", "image path: $imagePath")
        val target: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                visibility = View.VISIBLE

                //get measured image size
                val imageWidth = bitmap.width
                val imageHeight = bitmap.height
                setImageBitmap(bitmap)
                Log.d("Bitmap Dimensions: ", imageWidth.toString() + "x" + imageHeight)
                var params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.gravity = Gravity.CENTER
                val imageHorizontalMargin =
                    getImageHorizontalMargin(imageWidth.toFloat() / imageHeight, context) // in px
                val imageVerticalMargin = dpConvertPx(60, context)
                params.setMargins(
                    imageHorizontalMargin,
                    imageVerticalMargin,
                    imageHorizontalMargin,
                    0
                )
                layoutParams = params

                // reset bottom margin
                val textHeight = getHeight(context, title)
                val textWhitespace = (imageVerticalMargin + imageHeight) / 3 - textHeight
                Log.d("whitespace", textWhitespace.toString())
                Log.d("whitespace_text", textHeight.toString())
                val textTopMargin = (textWhitespace / (1 + 1.5)).toInt()
                val textBottomMargin = (textWhitespace * 1.5 / (1 + 1.5)).toInt()
                params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                params.setMargins(
                    dpConvertPx(18, context),
                    textTopMargin,
                    dpConvertPx(18, context),
                    textBottomMargin
                )
                title.layoutParams = params
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            // load image and resize it
            // action wil be done when load the image

        }
        // set image
        tag = target
        Picasso.get().load(imagePath).into(target)
    }
}


@BindingAdapter("commentUsername")
fun TextView.setCommentUsername(item: String?) {
    text = item?: "User"
}

@BindingAdapter("commentDate")
fun TextView.setDate(item: String) {
    text = convertTimeStamp(item, "COMMENT")
}

@BindingAdapter("commentContent")
fun TextView.setContent(item: String) {
    text = item
}

@BindingAdapter("commentReadMore")
fun TextView.setCommentReadMore(item: Comment) {
    // todo (Not implemented)
}


@BindingAdapter("android:src")
fun ImageView.setSrc(isLiked: Boolean) {
    tag = if (isLiked) {
        setImageResource(R.drawable.ic_heart_filled)
        "liked"
    } else {
        setImageResource(R.drawable.ic_heart)
        "like"
    }
}


@BindingAdapter("app:comment")
fun RecyclerView.setSubComment(item: Comment) {
    // set sub comments recycler view
    /*
    val subCommentAdapter: SubCommentsAdapter = SubCommentsAdapter()
    apply {
        layoutManager = LinearLayoutManager(context)
        adapter = subCommentAdapter
        setRecycledViewPool(RecyclerView.RecycledViewPool())
        setHasFixedSize(true)
    }
    item.subComments?.let {
        subCommentAdapter.submitList(it)
    }
     */
}





