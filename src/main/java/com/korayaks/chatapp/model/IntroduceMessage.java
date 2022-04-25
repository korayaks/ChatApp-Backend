package com.korayaks.chatapp.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class IntroduceMessage {
    private String senderName;
    private String receiverName;
    private String[] message;
}
