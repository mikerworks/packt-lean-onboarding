package com.packt.tellastory.data

import com.packt.tellastory.models.Story

interface OnRepositoryResult {

    fun onResult(result: List<Story>)
}
