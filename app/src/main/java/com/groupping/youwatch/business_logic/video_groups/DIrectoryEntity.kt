package com.groupping.youwatch.business_logic.video_groups

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.groupping.youwatch.business_logic.video.ListItem

@Entity(tableName = "directories")
data class DirectoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generated ID for each directory
    val directoryName: String,
    val parentId: Int? // Nullable to handle root directory with no parent
)

fun List<DirectoryEntity>.toListItems(): List<ListItem> {
    return this.map { ListItem.Directory(it) }
}