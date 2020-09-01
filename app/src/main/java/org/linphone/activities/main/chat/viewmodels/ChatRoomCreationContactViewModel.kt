/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.activities.main.chat.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.contact.Contact
import org.linphone.contact.ContactViewModelInterface
import org.linphone.core.*
import org.linphone.utils.LinphoneUtils

class ChatRoomCreationContactViewModel(private val searchResult: SearchResult) : ViewModel(), ContactViewModelInterface {
    override val contact = MutableLiveData<Contact>()

    override val displayName: String by lazy {
        val address = searchResult.address
        searchResult.friend?.name ?: if (address != null) LinphoneUtils.getDisplayName(address) else searchResult.phoneNumber.orEmpty()
    }

    val isDisabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSelected: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isLinphoneUser: Boolean by lazy {
        searchResult.friend?.getPresenceModelForUriOrTel(searchResult.phoneNumber ?: searchResult.address?.asStringUriOnly() ?: "")?.basicStatus == PresenceBasicStatus.Open
    }

    val sipUri: String by lazy {
        searchResult.phoneNumber ?: LinphoneUtils.getDisplayableAddress(searchResult.address)
    }

    val address: Address? by lazy {
        searchResult.address
    }

    val hasLimeX3DHCapability: Boolean
        get() = searchResult.hasCapability(FriendCapability.LimeX3Dh)

    var listener: ChatRoomCreationContactSelectionListener? = null

    init {
        isDisabled.value = false
        isSelected.value = false
        searchMatchingContact()
    }

    private fun searchMatchingContact() {
        val address = searchResult.address
        if (address != null) {
            contact.value = coreContext.contactsManager.findContactByAddress(address)
        } else if (searchResult.phoneNumber != null) {
            contact.value = coreContext.contactsManager.findContactByPhoneNumber(searchResult.phoneNumber.orEmpty())
        }
    }
}

interface ChatRoomCreationContactSelectionListener {
    fun onUnSelected(searchResult: SearchResult)
}
