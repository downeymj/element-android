/*
 * Copyright (c) 2021 New Vector Ltd
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

package im.vector.app.features.createpoll

import com.airbnb.epoxy.EpoxyController
import im.vector.app.R
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.ItemStyle
import im.vector.app.core.ui.list.genericButtonItem
import im.vector.app.core.ui.list.genericItem
import im.vector.app.features.form.formEditTextItem
import im.vector.app.features.form.formEditTextWithDeleteItem
import javax.inject.Inject

class CreatePollController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider
) : EpoxyController() {

    private var state: CreatePollViewState? = null
    var callback: Callback? = null

    fun setData(state: CreatePollViewState) {
        this.state = state
        requestModelBuild()
    }

    override fun buildModels() {
        val currentState = state ?: return
        val host = this

        genericItem {
            id("question_title")
            style(ItemStyle.BIG_TEXT)
            title(host.stringProvider.getString(R.string.create_poll_question_title))
        }

        formEditTextItem {
            id("question")
            value(currentState.question)
            hint(host.stringProvider.getString(R.string.create_poll_question_hint))
            singleLine(false)
            maxLength(500)
            onTextChange {
                host.callback?.onQuestionChanged(it)
            }
        }

        genericItem {
            id("options_title")
            style(ItemStyle.BIG_TEXT)
            title(host.stringProvider.getString(R.string.create_poll_options_title))
        }

        currentState.options.forEachIndexed { index, option ->
            formEditTextWithDeleteItem {
                id("option_$index")
                value(option)
                hint(host.stringProvider.getString(R.string.create_poll_options_hint, (index + 1)))
                onTextChange {
                    host.callback?.onOptionChanged(index, it)
                }
                onDeleteClicked {
                    host.callback?.onDeleteOption(index)
                }
            }
        }

        genericButtonItem {
            id("add_option")
            text(host.stringProvider.getString(R.string.create_poll_add_option))
            textColor(host.colorProvider.getColor(R.color.palette_element_green))
            buttonClickAction {
                host.callback?.onAddOption()
            }
        }
    }

    interface Callback {
        fun onQuestionChanged(question: String)
        fun onOptionChanged(index: Int, option: String)
        fun onDeleteOption(index: Int)
        fun onAddOption()
    }
}
