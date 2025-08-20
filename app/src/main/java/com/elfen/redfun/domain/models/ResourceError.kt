package com.elfen.redfun.domain.models

import com.elfen.redfun.presentation.utils.Resource

data class ResourceError(val error: Resource.Error<Any>): Error()