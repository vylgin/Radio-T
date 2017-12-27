package pro.vylgin.radiot.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.v4.app.ActivityCompat.startPostponedEnterTransition
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.model.system.ResourceManager
import retrofit2.HttpException
import java.io.IOException
import java.util.*


fun Resources.color(colorRes: Int) =
        if (Build.VERSION.SDK_INT >= 23) {
            this.getColor(colorRes, null)
        } else {
            this.getColor(colorRes)
        }

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun Throwable.userMessage(resourceManager: ResourceManager) = when (this) {
    is HttpException -> when (this.code()) {
        304 -> resourceManager.getString(R.string.not_modified_error)
        400 -> resourceManager.getString(R.string.bad_request_error)
        401 -> resourceManager.getString(R.string.unauthorized_error)
        403 -> resourceManager.getString(R.string.forbidden_error)
        404 -> resourceManager.getString(R.string.not_found_error)
        405 -> resourceManager.getString(R.string.method_not_allowed_error)
        409 -> resourceManager.getString(R.string.conflict_error)
        422 -> resourceManager.getString(R.string.unprocessable_error)
        500 -> resourceManager.getString(R.string.server_error_error)
        else -> resourceManager.getString(R.string.unknown_error)
    }
    is IOException -> resourceManager.getString(R.string.network_error)
    else -> resourceManager.getString(R.string.unknown_error)
}

private val DATE_FORMAT = DateTimeFormat.forPattern("dd.MM.yyyy")
fun Date.humanTime(): String {
    val localMillis = DateTimeZone.getDefault().convertUTCToLocal(this.time)
    return DATE_FORMAT.print(localMillis)
}

fun ImageView.loadImage(
        url: String?,
        ctx: Context? = null
) {
    Glide.with(context)
            .load(url)
            .apply(RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground))
            .transition(withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    if (ctx is AppCompatActivity) {
                        startPostponedEnterTransition(ctx)
                    }
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (ctx is AppCompatActivity) {
                        startPostponedEnterTransition(ctx)
                    }
                    return false
                }

            })
            .into(this)
}

fun TimeLabel.humanDuration(): String {
    if (duration == 0) {
        return ""
    }

    val minutes = duration / 60
    val seconds = duration % 60
    return "%d:%02d".format(minutes, seconds)
}

fun Entry.getTransitionNames(): List<String> {
    return listOf(url, "$url-1", "$url-2")
}

fun Entry.isEmpty(): Boolean {
    return url.isEmpty()
}