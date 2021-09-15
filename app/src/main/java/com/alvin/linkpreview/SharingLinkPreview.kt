package com.alvin.linkpreview

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*


/**
 * Created by Alex on 11/12/2015.
 */
class SharingLinkPreview : RelativeLayout {
    private lateinit var mImgViewImage: ImageView
    private lateinit var mPreviewRoot: CardView
    private lateinit var mTxtViewTitle: TextView
    private lateinit var mTxtViewDescription: TextView
    private lateinit var mTxtViewSiteName: TextView
    private lateinit var mTxtViewMessage: TextView
    private lateinit var mContext: Context
    private lateinit var mHandler: Handler
    private lateinit var mProcess: FrameLayout
    private lateinit var ivClose: ImageView

    var title: String? = null
        private set
    var description: String? = null
        private set
    var imageLink: String? = null
        private set
    var siteName: String? = null
        private set

    private var site: String? = null

    var link: String? = null
        private set

    private var mListener: PreviewListener? = null

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context,
        attrs,
        defStyle) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        mContext = context
        inflate(context, R.layout.preview_layout, this)
        mImgViewImage = findViewById<View>(R.id.imgViewImage) as ImageView
        ivClose = findViewById<View>(R.id.iv_close) as ImageView
        mPreviewRoot = findViewById<View>(R.id.preview_root) as CardView
        mTxtViewTitle = findViewById<View>(R.id.txtViewTitle) as TextView
        mTxtViewDescription = findViewById<View>(R.id.txtViewDescription) as TextView
        mTxtViewSiteName = findViewById<View>(R.id.txtViewSiteName) as TextView
        mTxtViewMessage = findViewById<View>(R.id.txtViewMessage) as TextView
        mProcess = findViewById<View>(R.id.progress_circular) as FrameLayout
        mHandler = Handler(mContext.mainLooper)
    }

    fun setListener(listener: PreviewListener?) {
        mListener = listener
    }

    /*fun setData(title: String?, description: String?, image: String?, site: String?) {
        clear()
        this.title = title
        this.description = description
        imageLink = image
        siteName = site
        if (title != null) {
            Log.v(TAG, title)
            if (title.length >= 50) this.title = title.substring(0, 49) + "..."
            runOnUiThread { mTxtViewTitle.text = title }
        }
        if (description != null) {
            Log.v(TAG, description)
            if (description.length >= 100) this.description = description.substring(0, 99) + "..."
            runOnUiThread { mTxtViewDescription.text = description }
        }
        if (imageLink != null && imageLink != "") {
            Log.v(TAG, imageLink!!)
            runOnUiThread {
                loadGlide(imageLink)
            }
        } else {
            runOnUiThread {
                loadGlide(R.drawable.no_image)
            }
        }
        siteName?.let {
            Log.v(TAG, it)
            if (it.length >= 30) siteName = it.substring(0, 29) + "..."
            runOnUiThread { mTxtViewSiteName.text = it }
        }
    }
*/
    fun setData(url: String) {

        if (!TextUtils.isEmpty(url)) {
            runOnUiThread {
                mProcess.visibility = VISIBLE
            }
            clear()
            val client = OkHttpClient()

            try {
                val request: Request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(request: Request?, throwable: IOException) {
                        if (!TextUtils.isEmpty(throwable.message)) {
                            throwable.message?.let { Log.e(TAG, it) }
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(response: Response) {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val titleElements: Elements
                        val descriptionElements: Elements
                        val imageElements: Elements
                        val siteElements: Elements
                        var linkElements: Elements
                        var site = ""
                        val doc: Document = Jsoup.parse(response.body().string())
                        titleElements = doc.select("title")
                        descriptionElements = doc.select("meta[name=description]")
                        when {
                            url.contains("bhphotovideo") -> {
                                imageElements = doc.select("image[id=mainImage]")
                                site = "bhphotovideo"
                            }
                            url.contains("www.amazon.com") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.com"
                            }
                            url.contains("www.amazon.co.uk") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.co.uk"
                            }
                            url.contains("www.amazon.de") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.de"
                            }
                            url.contains("www.amazon.fr") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.fr"
                            }
                            url.contains("www.amazon.it") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.it"
                            }
                            url.contains("www.amazon.es") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.es"
                            }
                            url.contains("www.amazon.ca") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.ca"
                            }
                            url.contains("www.amazon.co.jp") -> {
                                imageElements = doc.select("img[data-old-hires]")
                                site = "www.amazon.co.jp"
                            }
                            url.contains("m.clove.co.uk") -> {
                                imageElements = doc.select("img[id]")
                                site = "m.clove.co.uk"
                            }
                            url.contains("www.clove.co.uk") -> {
                                imageElements = doc.select("li[data-thumbnail-path]")
                                site = "www.clove.co.uk"
                            }
                            else -> imageElements = doc.select("meta[property=og:image]")
                        }
                        imageLink = getImageLinkFromSource(imageElements, site)
                        siteElements = doc.select("meta[property=og:site_name]")
                        linkElements = doc.select("meta[property=og:url]")
                        if (titleElements != null && titleElements.size > 0) {
                            title = titleElements[0].text()
                        }
                        if (descriptionElements != null && descriptionElements.size > 0) {
                            description = descriptionElements[0].attr("content")
                        }
                        if (linkElements != null && linkElements.size > 0) {
                            link = linkElements[0].attr("content")
                        } else {
                            linkElements = doc.select("link[rel=canonical]")
                            if (linkElements != null && linkElements.size > 0) {
                                link = linkElements[0].attr("href")
                            }
                        }
                        if (siteElements != null && siteElements.size > 0) {
                            siteName = siteElements[0].attr("content")
                        }

                        title?.let {
                            Log.v(TAG, it)
                            if (it.length >= 50) title = it.substring(0, 49) + "..."

                            runOnUiThread {
                                mTxtViewTitle.text = title
                            }
                        }

                        description?.let {
                            Log.v(TAG, description!!)
                            if (it.length >= 100) description =
                                it.substring(0, 99) + "..."
                            runOnUiThread { mTxtViewDescription.text = description }
                        }
                        if (imageLink != null && imageLink != "") {
                            Log.v(TAG, imageLink!!)
                            runOnUiThread {
                                loadGlide(imageLink)
                            }
                        } else {
                            runOnUiThread {
                                loadGlide(R.drawable.no_image)
                            }
                        }
                        if (url.lowercase(Locale.getDefault())
                                .contains("amazon")
                        ) if (siteName == null || siteName == "") siteName = "Amazon"
                        if (siteName != null) {
                            Log.v(TAG, siteName!!)
                            if (siteName!!.length >= 30) siteName =
                                siteName!!.substring(0, 29) + "..."
                            runOnUiThread { mTxtViewSiteName.text = siteName }
                        }
                        Log.v(TAG, "Link: $link")
                        runOnUiThread {
                            // if (mLoadingDialog.isStart()) mLoadingDialog.stop()
                            // mFrameLayout!!.visibility = GONE
                            mProcess.visibility = GONE
                        }

                        runOnUiThread {
                            mListener?.onDataReady(this@SharingLinkPreview)
                            mPreviewRoot.setOnClickListener {
                                mListener?.onLinkClicked(link)
                            }
                            slideUp(mPreviewRoot)

                            ivClose.setOnClickListener {
                                slideDown(mPreviewRoot)
                            }
                        }
                    }
                })

            } catch (ex: Exception) {
                mListener?.onDataReady(this@SharingLinkPreview)
                runOnUiThread {
                    mPreviewRoot.visibility = GONE
                    mProcess.visibility = GONE
                }
            }
        }

    }

    fun setMessage(message: String?) {
        runOnUiThread {
            if (message == null) {
                mTxtViewMessage.visibility =
                    GONE
            } else mTxtViewMessage.visibility =
                VISIBLE
            mTxtViewMessage.text = message

        }
    }

    fun showLayout(message: String?) {
        runOnUiThread {
            when {
                message.isNullOrEmpty() -> {
                    mPreviewRoot.visibility = GONE
                }
                message == "null" -> {
                    mPreviewRoot.visibility = GONE
                }
                else -> {
                    mPreviewRoot.visibility = VISIBLE
                    slideUp(mPreviewRoot)
                }
            }
        }
    }

    private fun getImageLinkFromSource(elements: Elements?, site: String): String? {
        var imageLink: String? = null
        if (elements != null && elements.size > 0) {
            when (site) {
                "m.clove.co.uk", "bhphotovideo" -> imageLink = elements[0].attr("src")
                "www.amazon.com", "www.amazon.co.uk", "www.amazon.de", "www.amazon.fr", "www.amazon.it", "www.amazon.es", "www.amazon.ca", "www.amazon.co.jp" -> {
                    imageLink = elements[0].attr("data-old-hires")
                    if (TextUtils.isEmpty(imageLink)) {
                        imageLink = elements[0].attr("src")
                        if (imageLink.contains("data:image/jpeg;base64,")) {
                            imageLink = elements[0].attr("data-a-dynamic-image")
                            if (!TextUtils.isEmpty(imageLink)) {
                                val array = imageLink.split(":\\[").toTypedArray()
                                if (array.size > 1) {
                                    imageLink = array[0]
                                    if (!TextUtils.isEmpty(imageLink)) {
                                        imageLink = imageLink.replace("{\"", "")
                                        imageLink = imageLink.replace("\"", "")
                                    }
                                }
                            }
                        }
                    }
                }
                "www.clove.co.uk" -> imageLink =
                    "https://www.clove.co.uk" + elements[0].attr("data-thumbnail-path")
                else -> imageLink = elements[0].attr("content")
            }
        }
        return imageLink
    }

    private fun clear() {
        mImgViewImage.setImageResource(0)
        mTxtViewTitle.text = ""
        mTxtViewDescription.text = ""
        mTxtViewSiteName.text = ""
        mTxtViewMessage.text = ""
        title = null
        description = null
        imageLink = null
        siteName = null
        site = null
        link = null
    }

    interface PreviewListener {
        fun onDataReady(preview: SharingLinkPreview?)
        fun onLinkClicked(link: String?)
    }

    private fun runOnUiThread(runnable: Runnable) {
        mHandler.post(runnable)
    }

    fun loadGlide(imageLink: String?) {
        Glide.with(mContext)
            .load(imageLink)
            .into(mImgViewImage)
    }

    fun loadGlide(imageLink: Int?) {
        Glide.with(mContext)
            .load(imageLink)
            .into(mImgViewImage)
    }

    companion object {
        private const val TAG = "Preview"
    }

    private fun slideUp(views: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            views.height.toFloat(),  // fromYDelta
            0f  // toYDelta
        )

        animate.duration = 300
        animate.fillAfter = true
        views.startAnimation(animate)
        views.visibility = VISIBLE
    }

    private fun slideDown(views: View) {

        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            views.height.toFloat()  // toYDelta
        )

        animate.duration = 400
        animate.fillAfter = true
        views.startAnimation(animate)

        views.animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                views.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

    }
}