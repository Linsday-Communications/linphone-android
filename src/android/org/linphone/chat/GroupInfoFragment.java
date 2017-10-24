/*
InfoGroupChatFragment.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.linphone.chat;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.activities.LinphoneActivity;
import org.linphone.contacts.ContactAddress;
import org.linphone.core.Address;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomListenerStub;
import org.linphone.mediastream.Log;

import java.util.ArrayList;

public class GroupInfoFragment extends Fragment {
	private ImageView mBackButton, mConfirmButton, mAddParticipantsButton;
	private EditText mSubjectField;
	private LayoutInflater mInflater;
	private ListView mParticipantsList;
	private LinearLayout mLeaveGroupButton;
	private GroupInfoAdapter mAdapter;
	private boolean mIsAlreadyCreatedGroup;
	private boolean mIsEditionEnabled;
	private ArrayList<ContactAddress> mParticipants;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.chat_infos, container, false);

		if (getArguments() == null || getArguments().isEmpty()) {
			return null;
		}
		mParticipants =  (ArrayList<ContactAddress>) getArguments().getSerializable("ContactAddress");
		mIsAlreadyCreatedGroup = getArguments().getBoolean("isAlreadyCreatedGroup");
		mIsEditionEnabled = getArguments().getBoolean("isEditionEnabled");
		String subject = getArguments().getString("subject");

		mParticipantsList = view.findViewById(R.id.chat_room_participants);
		mAdapter = new GroupInfoAdapter(mInflater, mParticipants, !mIsEditionEnabled, !mIsAlreadyCreatedGroup);
		mAdapter.setOnDeleteClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ContactAddress ca = (ContactAddress) view.getTag();
				mParticipants.remove(ca);
				mAdapter.updateDataSet(mParticipants);
				mParticipantsList.setAdapter(mAdapter);
				mConfirmButton.setEnabled(mSubjectField.getText().length() > 0 && mParticipants.size() > 0);
			}
		});
		mParticipantsList.setAdapter(mAdapter);

		mBackButton = view.findViewById(R.id.back);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LinphoneActivity.instance().goToChatCreator(mParticipants);
			}
		});

		mConfirmButton = view.findViewById(R.id.confirm);

		mLeaveGroupButton = view.findViewById(R.id.leaveGroupLayout);
		mLeaveGroupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//TODO
			}
		});
		mLeaveGroupButton.setVisibility(mIsAlreadyCreatedGroup ? View.VISIBLE : View.GONE);

		mAddParticipantsButton = view.findViewById(R.id.addParticipants);
		mAddParticipantsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mIsAlreadyCreatedGroup) {
					//TODO
				} else {
					LinphoneActivity.instance().goToChatCreator(mParticipants);
				}
			}
		});

		mSubjectField = view.findViewById(R.id.subjectField);
		mSubjectField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				mConfirmButton.setEnabled(mSubjectField.getText().length() > 0 && mParticipants.size() > 1);
			}
		});
		mSubjectField.setText(subject);

		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mIsAlreadyCreatedGroup) {
					ChatRoom chatRoom = LinphoneManager.getLc().createClientGroupChatRoom(mSubjectField.getText().toString());
					chatRoom.setListener(new ChatRoomListenerStub() {
						@Override
						public void onStateChanged(ChatRoom cr, ChatRoom.State newState) {
							if (newState == ChatRoom.State.Created) {
								LinphoneActivity.instance().goToChat(cr.getConferenceAddress().asStringUriOnly());
							} else if (newState == ChatRoom.State.CreationFailed) {
								//TODO display error
								Log.e("Group chat room for address " + cr.getConferenceAddress() + " has failed !");
							}
						}
					});

					Address addresses[] = new Address[mParticipants.size()];
					int index = 0;
					for (ContactAddress ca : mParticipants) {
						addresses[index] = LinphoneManager.getLc().createAddress(ca.getAddress());
						index++;
					}
					chatRoom.addParticipants(addresses);
				} else {
					//TODO
				}
			}
		});
		mConfirmButton.setEnabled(mSubjectField.getText().length() > 0 && mParticipants.size() > 0);

		if (!mIsEditionEnabled) {
			mSubjectField.setEnabled(false);
			mConfirmButton.setVisibility(View.INVISIBLE);
			mAddParticipantsButton.setVisibility(View.GONE);
		}

		return view;
	}
}