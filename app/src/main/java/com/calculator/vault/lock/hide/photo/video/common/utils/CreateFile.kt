package com.calculator.vault.lock.hide.photo.video.common.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.FileChannel
import java.util.*

class CreateFile(
    val context: Context,
    val contentResolver: ContentResolver,
    val path: String,
    val rootUri: Uri,
    val rootDocumentId: String,
    val isCopyOepration: Boolean = false,
    val copyFilePath: Any?,
    val isImage: Boolean = false,
    val mimeType: String = "*/*"
) {
    lateinit var blankUri: Uri

    public fun createNewFile(
        makeDirectories: Boolean = false,
        overwriteIfExists: Boolean = false
    ): Boolean {
        return traverse({ dir, nextDocId, childrenUri ->
            if (nextDocId == "") {
                if (makeDirectories) {
                    getChildrenUri(
                        DocumentsContract.createDocument(
                            contentResolver,
                            childrenUri,
                            DocumentsContract.Document.MIME_TYPE_DIR,
                            dir
                        )!!
                    )
                } else {
                    throw DirectoryHierarchyBroken("No such file or directory")
                }
            } else getChildrenUri(nextDocId)
        }, { fileName, nextDocId, childrenUri ->
            return@traverse if (nextDocId != "") {
                val existingUri = buildTreeDocumentUriFromId(nextDocId)
                if (overwriteIfExists) {
                    DocumentsContract.deleteDocument(contentResolver, existingUri)
                    createBlankDoc(childrenUri, fileName)
                } else {
                    //                    setPathAndUri(rootUri!!, path, existingUri)
                    false
                }
            } else {
                createBlankDoc(childrenUri, fileName)
            }
        })
    }

    private fun traverse(
        directoryFunc: (dirName: String, nextDocId: String, childrenUri: Uri) -> Uri?,
        fileFunction: (fileName: String, nextDocId: String, childrenUri: Uri) -> Boolean
    ): Boolean {
        val dirs = if (path.length > 1) path.substring(1).split("/") else ArrayList(0)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_DOCUMENT_ID
        )
        var childrenUri = getChildrenUri(rootUri)
        for (i in dirs.indices) {
            val dir = dirs[i]
            var nextDocId = ""
            contentResolver.query(childrenUri, projection, null, null, null)?.run {
                while (moveToNext()) {
                    if (getString(0) == dir) {
                        nextDocId = getString(1)
                        break
                    }
                }
                close()
            }
            if (i < dirs.indices.last) directoryFunc(
                dir,
                nextDocId,
                childrenUri
            ).let { if (it != null) childrenUri = it else return false }
            else return fileFunction(dirs.last(), nextDocId, childrenUri)
        }
        return false
    }

    private fun getChildrenUri(docId: Uri?): Uri {
        return DocumentsContract.buildChildDocumentsUriUsingTree(
            rootUri,
            if (docId == rootUri) DocumentsContract.getTreeDocumentId(rootUri)
            else DocumentsContract.getDocumentId(docId)
        )
    }

    private fun getChildrenUri(docId: String): Uri {
        return DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
    }

    private fun buildTreeDocumentUriFromId(documentId: String): Uri {
        Log.d("TAG1", "buildTreeDocumentUriFromId: ")
        return Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(rootUri!!.authority)
            .appendPath("tree").appendPath(rootDocumentId)
            .appendPath("document").appendPath(documentId)
            .build()
    }

    private fun convertToDocumentUri(uri: Uri): Uri? {
        return if (DocumentsContract.isDocumentUri(context, uri)) uri
        else try {
            DocumentsContract.buildDocumentUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun checkUriExists(uri: Uri, checkIfDirectory: Boolean = false): Boolean {
        var result = false
        val evalUri = convertToDocumentUri(uri) ?: return false
        val projection =
            if (checkIfDirectory) arrayOf(DocumentsContract.Document.COLUMN_MIME_TYPE) else null
        try {
            val c = contentResolver.query(evalUri, projection, null, null, null, null)
            if (c != null && c.count > 0 && c.moveToFirst()) result = c.columnCount != 0
            if (result && checkIfDirectory) result =
                c?.getString(0) == DocumentsContract.Document.MIME_TYPE_DIR
            c?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun createBlankDoc(
        parentUri: Uri,
        fileName: String

    ): Boolean {
        Log.d(
            "TAG1", "createBlankDoc: + $fileName " +
                    "parent Uri $parentUri"
        )
        if (!parentUri.toString()
                .endsWith("/children") && !checkUriExists(parentUri)
        ) throw DirectoryHierarchyBroken(
            "Complete parent uri not present: $parentUri"
        )
        return DocumentsContract.createDocument(
            contentResolver,
            parentUri,
            mimeType,
            fileName
        ).let {
            if (it != null) {
                blankUri = it
            }
            if (isCopyOepration) {
                var inputStream: InputStream? = null
                if (copyFilePath is String) {
                    inputStream = FileInputStream(copyFilePath)
                } else {
                    inputStream =
                        contentResolver.openInputStream(copyFilePath as Uri) as FileInputStream
                }
                val outputStream = contentResolver.openOutputStream(it!!) as FileOutputStream
                if (isImage) {
                    val src: FileChannel = inputStream.channel
                    val dst: FileChannel = outputStream.channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()

                } else {
                    val inChannel: FileChannel = inputStream.channel
                    val outChannel: FileChannel = outputStream.channel
                    inChannel.transferTo(0, inChannel.size(), outChannel)
                }
                inputStream.close()
                outputStream.close()
                true
            } else {
                it != null
            }
        }
    }

}