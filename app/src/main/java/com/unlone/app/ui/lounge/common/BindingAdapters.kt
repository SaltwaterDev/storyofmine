package com.unlone.app.ui.lounge.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.unlone.app.R
import com.unlone.app.model.*
import com.unlone.app.utils.convertTimeStamp
import com.unlone.app.utils.dpConvertPx
import com.unlone.app.utils.getImageHorizontalMargin
import java.util.*

object BindingAdapters {
    @BindingAdapter("android:onClick")
    @JvmStatic fun MaterialCardView.setOnClick(item: Post) {
        setOnClickListener {
            val intent = Intent(context, PostDetailFragment::class.java)
            intent.putExtra("postId", item.pid)
            context.startActivity(intent)
        }
    }


    @BindingAdapter("journal")
    @JvmStatic fun TextView.setJournal(item: Post) {
        text = item.journal
    }

    @BindingAdapter("postImage")
    @JvmStatic fun ImageView.setPostImage(imagePath: String?) {
        try {
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    visibility = View.VISIBLE
                    //get measured image size
                    val imageWidth = bitmap.width
                    val imageHeight = bitmap.height
                    setImageBitmap(bitmap)
                    val imageVerticalMargin = dpConvertPx(60, context)
                    layoutParams =
                        getImageParams(context, imageWidth, imageHeight, imageVerticalMargin)
                }

                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            }
            tag = target
            Picasso.get().load(imagePath).into(target)
        } catch (e: Exception) {
            visibility = View.GONE
        }
    }

    @BindingAdapter("postImage", "title")
    @JvmStatic fun ImageView.setPostImage(imagePath: String, title: TextView) {
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
                    val imageVerticalMargin = dpConvertPx(60, context)
                    layoutParams =
                        getImageParams(context, imageWidth, imageHeight, imageVerticalMargin)

                    // reset bottom margin
                    val textHeight = getHeight(context, title)
                    Log.d("whitespace_text", textHeight.toString())
                    title.layoutParams =
                        getTitleParams(context, textHeight, imageHeight, imageVerticalMargin)
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

    fun getHeight(context: Context, textView: TextView): Int {
        val displayMetrics = context.resources.displayMetrics
        val deviceHeight = displayMetrics.heightPixels
        val deviceWidth = displayMetrics.widthPixels
        val widthMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }

    fun getImageParams(
        context: Context,
        imageWidth: Int,
        imageHeight: Int,
        imageVerticalMargin: Int
    ): LinearLayout.LayoutParams {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER
        val imageHorizontalMargin =
            getImageHorizontalMargin(imageWidth.toFloat() / imageHeight, context) // in px
        params.setMargins(
            imageHorizontalMargin,
            imageVerticalMargin,
            imageHorizontalMargin,
            0
        )
        return params
    }

    fun getTitleParams(
        context: Context,
        textHeight: Int,
        imageHeight: Int,
        imageVerticalMargin: Int
    ): ViewGroup.LayoutParams {
        val textWhitespace = (imageVerticalMargin + imageHeight) / 3 - textHeight
        Log.d("whitespace", textWhitespace.toString())
        val textTopMargin = (textWhitespace / (1 + 1.5)).toInt()
        val textBottomMargin = (textWhitespace * 1.5 / (1 + 1.5)).toInt()
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            dpConvertPx(18, context),
            textTopMargin,
            dpConvertPx(18, context),
            textBottomMargin
        )
        return params
    }

    @BindingAdapter("commentUsername")
    @JvmStatic fun TextView.setCommentUsername(item: String?) {
        text = item ?: "User"
    }

    @BindingAdapter("date")
    @JvmStatic fun TextView.setDate(item: String?) {
        text = item?.let { convertTimeStamp(it, Locale.getDefault().language) }
    }

    @BindingAdapter("commentDate")
    @JvmStatic fun TextView.setCommentDate(item: String) {
        text = convertTimeStamp(item, "COMMENT")
    }

    @BindingAdapter("commentContent")
    @JvmStatic fun TextView.setContent(item: String) {
        text = item
    }

    @BindingAdapter("commentReadMore")
    @JvmStatic fun TextView.setCommentReadMore(item: Comment) {
        // todo (Not implemented)
    }


    @BindingAdapter("android:src")
    @JvmStatic fun ImageView.setSrc(isLiked: Boolean) {
        tag = if (isLiked) {
            setImageResource(R.drawable.ic_heart_filled)
            "liked"
        } else {
            setImageResource(R.drawable.ic_heart)
            "like"
        }
    }
}
