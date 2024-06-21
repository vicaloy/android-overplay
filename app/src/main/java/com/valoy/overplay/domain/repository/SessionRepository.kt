package com.valoy.overplay.domain.repository

interface SessionRepository {
    suspend fun saveTime(session: Long)
    suspend fun getTime(): Long
    suspend fun saveCount(session: Int)
    suspend fun getCount(): Int
}