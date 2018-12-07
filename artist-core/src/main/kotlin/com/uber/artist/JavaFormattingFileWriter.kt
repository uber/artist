/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.artist

import com.google.common.base.Preconditions.checkArgument
import com.google.common.io.CharSink
import com.google.common.io.CharSource
import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.FormatterException
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files

class JavaFormattingFileWriter(val outputFile: JavaFile, val typeSpecBuilder: TypeSpec.Builder, val packageName: String) : FormattingFileWriter<JavaFile, TypeSpec.Builder>() {

  private val formatter: Formatter = Formatter()

  /**
   * A file writer function that formats the code before writing out to the file system.
   */
  override fun writeWithFormattingTo(directory: File) {
    val directoryPath = directory.toPath()
    checkArgument(Files.notExists(directoryPath) || Files.isDirectory(directoryPath),
        "path %s exists but is not a directoryPath.", directoryPath)
    var outputDirectory = directoryPath
    if (!packageName.isEmpty()) {
      for (packageComponent in packageName.split(packageSplitRegex)
          .filter { !it.isEmpty() }.toTypedArray()) {
        outputDirectory = outputDirectory.resolve(packageComponent)
      }
      Files.createDirectories(outputDirectory)
    }

    val typeSpec = typeSpecBuilder.build()
    val outputPath = outputDirectory.resolve(typeSpec.name + ".java")
    try {
      OutputStreamWriter(Files.newOutputStream(outputPath), UTF_8).use { writer ->
        val stringBuilder = StringBuilder(defaultFileSize)
        outputFile.writeTo(stringBuilder)
        formatter.formatSource(
            CharSource.wrap(stringBuilder),
            object : CharSink() {
              @Throws(IOException::class)
              override fun openStream(): Writer {
                return writer
              }
            })
      }
    } catch (e: FormatterException) {
      throw IOException("Error formatting " + outputPath.fileName.toString(), e)
    }
  }
}
