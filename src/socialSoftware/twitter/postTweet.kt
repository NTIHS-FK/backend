package socialSoftware.twitter

import com.ntihs_fk.drawImage.draw
import com.ntihs_fk.util.Config
import twitter4j.StatusUpdate
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

fun postTweet(text: String, imagePath: String? = null, textImage: String, date: Date) {
    val mediaIds = mutableListOf<Long>()
    val cb = ConfigurationBuilder()

    cb.setDebugEnabled(false)
        .setOAuthConsumerKey(Config.twitterConfig.consumerKey)
        .setOAuthConsumerSecret(Config.twitterConfig.consumerSecret)
        .setOAuthAccessToken(Config.twitterConfig.accessToken)
        .setOAuthAccessTokenSecret(Config.twitterConfig.accessTokenSecret)

    val tf = TwitterFactory(cb.build())
    val twitter = tf.instance

    // text
    val statusUpdate = StatusUpdate(text)

    // text image
    val textImageMedia = twitter.uploadMedia(textImage, ByteArrayInputStream(draw("default")(text, date)))
    mediaIds.add(textImageMedia.mediaId)

    // image
    if (imagePath != null) {
        val imageFile = File("./image/$imagePath")
        val imageMedia = twitter.uploadMedia(imageFile)
        mediaIds.add(imageMedia.mediaId)
    }

    statusUpdate.setMediaIds(*mediaIds.toLongArray())
    twitter.updateStatus(statusUpdate)
}