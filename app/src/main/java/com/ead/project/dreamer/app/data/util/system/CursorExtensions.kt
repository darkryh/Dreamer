package com.ead.project.dreamer.app.data.util.system

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.database.Cursor
import com.ead.project.dreamer.data.models.Download


@SuppressLint("Range")
fun Cursor.downloadItem() : Download = getObject(getDescription())

@SuppressLint("Range")
fun Cursor.getDescription(): String = getString(getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))

@SuppressLint("Range")
fun Cursor.getStatus() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))

@SuppressLint("Range")
fun Cursor.getId() : Long = getLong(getColumnIndex(DownloadManager.COLUMN_ID))

@SuppressLint("Range")
fun Cursor.getCurrentDownloaded() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

@SuppressLint("Range")
fun Cursor.getTotalDownloaded() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))