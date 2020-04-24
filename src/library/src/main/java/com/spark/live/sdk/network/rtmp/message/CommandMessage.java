package com.spark.live.sdk.network.rtmp.message;

import com.spark.live.sdk.network.rtmp.message.commands.Command;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class CommandMessage extends RtmpMessage {

    private Command command = null;

    public CommandMessage() {
    }

    public CommandMessage(MessageHeader header, Command command) {
        this.header = header;
        this.command = command;
    }

    @Override
    public byte[] toBinary() {
        if (payload == null) {
            payload = command.toBinary();
        }
        return payload;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "CommandMessage{" +
                "header=" + header.toString() +
                "fields=" + command.toString() +
                '}';
    }
}
