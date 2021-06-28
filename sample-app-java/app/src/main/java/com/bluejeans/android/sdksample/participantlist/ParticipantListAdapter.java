/*
 * Copyright (c) 2021 Blue Jeans Network, Inc. All rights reserved.
 */
package com.bluejeans.android.sdksample.participantlist;

import static com.bluejeans.android.sdksample.participantlist.ParticipantListFragment.EVERYONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluejeans.android.sdksample.R;
import com.bluejeans.bluejeanssdk.meeting.ParticipantsService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.participantViewHolder> {
    private String TAG = "ParticipantListAdapter";
    private ArrayList<ParticipantsService.Participant> participantList = new ArrayList();
    private Context mContext;
    private ParticipantsService.Participant everyone = null;
    private boolean isForChat = false;
    private ParticipantChatItemListener chatItemListener;
    private CompositeDisposable disposable = new CompositeDisposable();

    public ParticipantListAdapter(Context context) {
        mContext = context;
    }

    public ParticipantListAdapter(Context context, boolean isChat, ParticipantChatItemListener participantChatItemListener) {
        mContext = context;
        isForChat = isChat;
        chatItemListener = participantChatItemListener;
    }

    @NonNull
    @Override
    public participantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemLayoutView = inflater.inflate(R.layout.layout_participant_item, parent, false);
        return new participantViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull participantViewHolder holder, int position) {
        holder.bind(participantList.get(position));
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public void updateMeetingList(List<ParticipantsService.Participant> participantList) {
        this.participantList.clear();
        this.disposable.dispose();
        disposable = new CompositeDisposable();
        if (isForChat && !participantList.contains(everyone)) {
            everyone = new ParticipantsService.Participant(EVERYONE);
            this.participantList.add(everyone);
        }
        this.participantList.addAll(participantList);
        if (isForChat) {
            // remove self participant
            this.participantList.remove(1);
        }
        notifyDataSetChanged();
    }

    class participantViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        TextView mParticipantName;
        ImageView mAudioState;
        ImageView mVideoState;
        ImageView mChatArrow;
        TextView mUnreadCount;

        public participantViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mParticipantName = itemView.findViewById(R.id.tvParticipantName);
            mAudioState = itemView.findViewById(R.id.ivRosterAudioStatus);
            mVideoState = itemView.findViewById(R.id.ivRosterVideoStatus);
            mChatArrow = itemView.findViewById(R.id.ivPrivateChat);
            mUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
        }

        public void bind(ParticipantsService.Participant participant) {
            if (participant.getId().equals(EVERYONE)) {
                mParticipantName.setText(EVERYONE);
            } else {
                mParticipantName.setText(participant.getName());
            }
            mAudioState.setSelected(participant.isAudioMuted());
            mVideoState.setSelected(participant.isVideoMuted());
            if (isForChat) {
                mAudioState.setVisibility(View.GONE);
                mVideoState.setVisibility(View.GONE);
                mChatArrow.setVisibility(View.VISIBLE);
                chatItemListener.onMessageCountChange(participant, mUnreadCount);
                mChatArrow.setOnClickListener(v -> {
                    chatItemListener.onParticipantClick(participant);
                });
            } else {
                mAudioState.setVisibility(View.VISIBLE);
                mVideoState.setVisibility(View.VISIBLE);
                mChatArrow.setVisibility(View.GONE);
            }
        }
    }
}
