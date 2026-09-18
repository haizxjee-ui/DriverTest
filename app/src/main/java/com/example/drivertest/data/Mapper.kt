package com.example.drivertest.data

import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.local.entity.QuestionEntity
import com.example.drivertest.data.local.entity.QuestionTypeEntity
import com.example.drivertest.data.remote.dto.QuestionDto
import com.example.drivertest.data.remote.dto.QuestionTypeDto

fun QuestionDto.toQuestionEntity(): QuestionEntity {
    return QuestionEntity(
        questionID = id,
        questionTypeID = questionType,
        content = question,
        explanation = suggest,
        isCritical = critical
    )
}

fun QuestionDto.toAnswerEntities(): List<AnswerEntity> {

    val options = listOf(
        "a" to option.a,
        "b" to option.b,
        "c" to option.c,
        "d" to option.d,
        "e" to option.e
    )

    return options.mapNotNull { (key, content) ->

        content?.let {
            AnswerEntity(
                questionID = id,
                content = it,
                isCorrect = key == answer
            )
        }
    }
}

fun QuestionDto.toImageEntities(): List<ImageEntity> {

    val images = listOf(
        1 to image?.img1,
        2 to image?.img2,
        3 to image?.img3,
        4 to image?.img4
    )

    return images.mapNotNull { (order, url) ->

        url?.let {
            ImageEntity(
                questionID = id,
                imageUrl = it,
                imageOrder = order
            )
        }
    }
}

fun QuestionTypeDto.toEntity(): QuestionTypeEntity {
    return QuestionTypeEntity(
        questionTypeID = id,
        name = title
    )
}