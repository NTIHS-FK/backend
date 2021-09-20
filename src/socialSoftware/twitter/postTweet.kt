package socialSoftware.twitter

import twitter4j.StatusUpdate
import twitter4j.TwitterFactory
import java.io.File

fun postTweet(text: String, imagePath: String? = null, textImage: String) {
    val mediaIds = mutableListOf<Long>()
    val twitter = TwitterFactory.getSingleton()

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