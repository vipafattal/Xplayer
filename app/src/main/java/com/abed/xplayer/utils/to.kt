package com.abed.xplayer.utils

import com.abed.xplayer.model.Item

infix fun String.to(link: String)= Item(this, link)
