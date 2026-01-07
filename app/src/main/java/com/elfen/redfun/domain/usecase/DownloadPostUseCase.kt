package com.elfen.redfun.domain.usecase

import android.content.Context
import android.widget.Toast
import com.elfen.redfun.domain.repository.FeedRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.URL
import javax.inject.Inject

class DownloadPostUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(postId: String) {
        val (post) = feedRepository.getPostWithComments(postId).first()
        if ((post.images?.size ?: 0) > 1) {
            // Download all images
            toast("Downloading all images...")
            post.images?.forEachIndexed { index, image ->
                val url = URL(image.source)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val cacheDir = context.cacheDir
                val fileName = "image_${post.id}_${(index + 1).toString().padStart(2, '0')}.jpg"
                val file = cacheDir.resolve(fileName)

                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()
            }

            // Zip the images
            val zipFileName = "post_${post.id}_images.zip"
            val zipFile = context.cacheDir.resolve(zipFileName)
            zipFile.outputStream().use { outputStream ->
                java.util.zip.ZipOutputStream(outputStream).use { zipOut ->
                    post.images?.forEachIndexed { index, _ ->
                        val fileName =
                            "image_${post.id}_${(index + 1).toString().padStart(2, '0')}.jpg"
                        val file = context.cacheDir.resolve(fileName)
                        file.inputStream().use { inputStream ->
                            val zipEntry = java.util.zip.ZipEntry(file.name)
                            zipOut.putNextEntry(zipEntry)
                            inputStream.copyTo(zipOut)
                            zipOut.closeEntry()
                        }
                    }
                }
            }

            // Save the zip file to Downloads
            val downloadsDir =
                android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val finalZipFile = downloadsDir.resolve(zipFileName)
            zipFile.inputStream().use { inputStream ->
                finalZipFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            toast("Zip file saved to Downloads")
        } else {
            toast("Only multiple images download supported now")
        }
    }

    fun toast(message: String) {
        runBlocking(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}