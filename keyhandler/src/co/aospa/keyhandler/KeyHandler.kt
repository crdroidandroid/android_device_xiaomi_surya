/*
 * Copyright (C) 2021 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aospa.keyhandler

import android.content.Context
import android.view.KeyEvent

import com.android.internal.os.DeviceKeyHandler

class KeyHandler(private val context: Context) : DeviceKeyHandler {

    override fun handleKeyEvent(event: KeyEvent): KeyEvent? {
        return when (event.scanCode) {
            KEYCODE_SIDE_FP -> null
            else -> event
        }
    }

    companion object {
        private const val KEYCODE_SIDE_FP = 96
    }
}
