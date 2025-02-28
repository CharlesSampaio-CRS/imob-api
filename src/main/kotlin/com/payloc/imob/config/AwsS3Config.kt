package com.payloc.imob.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManager
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config {

    @Bean
    fun amazonS3(): AmazonS3 {
        val secret = getSecretFromAWS("aws-credentials-us-east-1")
        val accessKeyId = secret["AWS_ACCESS_KEY_ID"].toString()
        val secretKey = secret["AWS_SECRET_ACCESS_KEY"].toString()
        val region = secret["AWS_REGION"].toString()

        val credentials = BasicAWSCredentials(accessKeyId, secretKey)
        return AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build()
    }

    private fun getSecretFromAWS(secretName: String): Map<String, Any> {
        val client: AWSSecretsManager = AWSSecretsManagerClientBuilder.standard().build()
        val getSecretValueRequest = com.amazonaws.services.secretsmanager.model.GetSecretValueRequest().withSecretId(secretName)
        val getSecretValueResponse = client.getSecretValue(getSecretValueRequest)
        val secretString = getSecretValueResponse.secretString
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(secretString, Map::class.java) as Map<String, Any>
    }
}
