package ru.rain.ifmo.myvkmessenger.domain.service

sealed class ServerResponseValidation

object SuccessfulResponse: ServerResponseValidation()

class FailureNewTs(val ts: Int): ServerResponseValidation()

object FailureNewServer: ServerResponseValidation()