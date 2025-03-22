package com.elfen.redfun.data.remote.models

import com.google.gson.annotations.SerializedName

data class Link(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subreddit") val subreddit: String,
    @SerializedName("author") val author: String,
    @SerializedName("score") val score: Int,
    @SerializedName("num_comments") val numComments: Int,
    @SerializedName("created") val created: Long,
    @SerializedName("is_reddit_media_domain") val isRedditMediaDomain: Boolean,
    @SerializedName("url_overridden_by_dest") val urlOverriddenByDest: String?,
    @SerializedName("removed_by_category") val removedByCategory: String?,
    @SerializedName("selftext") val selftext: String,
    @SerializedName("saved") val saved: Boolean,
    @SerializedName("over_18") val over18: Boolean,
    @SerializedName("permalink") val permalink: String,
    @SerializedName("gallery_data") val galleryData: GalleryData?,
    @SerializedName("media_metadata") val mediaMetadata: Map<String, MediaMetadata>?,
    @SerializedName("preview") val preview: Preview?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("thumbnail_height") val thumbnailHeight: Int?,
    @SerializedName("thumbnail_width") val thumbnailWidth: Int?,
    @SerializedName("is_video") val isVideo: Boolean?,
) {}

data class GalleryData(
    @SerializedName("items") val items: List<GalleryItem>
)

data class MediaMetadata(
    @SerializedName("id") val id: String?,
    @SerializedName("m") val m: String?,
    @SerializedName("p") val p: List<MediaMetadataImage>?,
    @SerializedName("s") val s: MediaMetadataImageSource?,
    @SerializedName("dashUrl") val dashUrl: String?,
    @SerializedName("x") val x: Int?,
    @SerializedName("y") val y: Int?,
    @SerializedName("hlsUrl") val hlsUrl: String?,
    @SerializedName("isGif") val isGif: Boolean?,
)

data class MediaMetadataImage(
    @SerializedName("y") val y: Int,
    @SerializedName("x") val x: Int,
    @SerializedName("u") val u: String,
)

data class MediaMetadataImageSource(
    @SerializedName("y") val y: Int,
    @SerializedName("x") val x: Int,
    @SerializedName("mp4") val mp4: String?,
    @SerializedName("gif") val gif: String?,
    @SerializedName("u") val u: String?,
)

data class ImageSource(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class GalleryItem(
    @SerializedName("media_id") val mediaId: String,
    @SerializedName("id") val id: Int
)

data class Preview(
    @SerializedName("images") val images: List<PreviewImage>,
    @SerializedName("reddit_video_preview") val redditVideoPreview: RedditVideoPreview?,
    @SerializedName("enabled") val enabled: Boolean?
)
data class PreviewImage(
    @SerializedName("reddit_video_preview") val reddit_video_preview: RedditVideoPreview?,
    @SerializedName("source") val source: ImageSource,
    @SerializedName("resolutions") val resolutions: List<ImageSource>,
    @SerializedName("id") val id: String
)
data class RedditVideoPreview(
    @SerializedName("hls_url") val hlsUrl: String?,
    @SerializedName("source") val source: ImageSource,
    @SerializedName("resolutions") val resolutions: List<ImageSource>,
    @SerializedName("id") val id: String
)


