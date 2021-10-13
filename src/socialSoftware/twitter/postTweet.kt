package socialSoftware.twitter

import com.ntihs_fk.util.Config
import twitter4j.StatusUpdate
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.io.File

fun postTweet(text: String, imagePath: String? = null, textImage: String) {
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
    val textImageFile = File("./textImage/$textImage.jpg")
    val textImageMedia = twitter.uploadMedia(textImageFile)
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