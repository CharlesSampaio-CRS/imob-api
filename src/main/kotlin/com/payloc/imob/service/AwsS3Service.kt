package com.payloc.imob.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AwsS3Service(
    private val amazonS3: AmazonS3
) {

    @Value("\${aws.s3.bucketName}")
    private lateinit var bucketName: String

    fun uploadImage(file: MultipartFile): String {
        val fileName = generateFileName(file.originalFilename!!)
        val metadata = ObjectMetadata().apply {
            contentLength = file.size
            contentType = file.contentType
        }

        val putObjectRequest = PutObjectRequest(bucketName, fileName, file.inputStream, metadata)


        amazonS3.putObject(putObjectRequest)
        return amazonS3.getUrl(bucketName, fileName).toString()
    }

    private fun generateFileName(originalFileName: String): String {
        return "${UUID.randomUUID()}_${originalFileName}"
    }
}