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
import org.linphone.activities.main.chat.GroupChatRoomMember
import org.linphone.contact.GenericContactViewModel
import org.linphone.core.ChatRoomSecurityLevel
import org.linphone.utils.LinphoneUtils

class GroupInfoParticipantViewModel(private val participant: GroupChatRoomMember) : GenericContactViewModel(participant.address) {
    override val securityLevel: ChatRoomSecurityLevel
        get() = participant.securityLevel

    val sipUri: String get() = LinphoneUtils.getDisplayableAddress(participant.address)

    val isAdmin = MutableLiveData<Boolean>()

    val showAdminControls = MutableLiveData<Boolean>()

    init {
        isAdmin.value = participant.isAdmin
        showAdminControls.value = false
    }

    fun setAdmin() {
        isAdmin.value = true
        participant.isAdmin = true
    }

    fun unSetAdmin() {
        isAdmin.value = false
        participant.isAdmin = false
    }
}
