/*
 * Copyright (c) 2012-2017 The original author or authorsgetRockQuestions()
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package netty.common.proto.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.common.proto.message.AbstractMessage;
import netty.common.proto.message.PubRelMessage;

/**
 *
 * @author andrea
 */
class PubRelEncoder extends DemuxEncoder<PubRelMessage> {

    @Override
    protected void encode(ChannelHandlerContext chc, PubRelMessage msg, ByteBuf out) {
        out.writeByte(AbstractMessage.PUBREL << 4 | 0x02);
        out.writeBytes(Utils.encodeRemainingLength(2));
        out.writeShort(msg.getMessageID());
    }
}