/*
 * Copyright (C) 2018. Uber Technologies
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

import java.io.File

abstract class FormattingFileWriter<OutputFileType, OutputType> {

  protected val packageSplitRegex = "\\.".toRegex()

  /**
   * A rough estimate of the average file size: 80 chars per line, 500 lines.
   */
  protected val defaultFileSize = 80 * 500

  /**
   * A file writer function that formats the code before writing out to the file system.
   */
  abstract fun writeWithFormattingTo(directory: File)
}
