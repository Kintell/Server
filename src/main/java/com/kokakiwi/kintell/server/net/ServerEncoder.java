package com.kokakiwi.kintell.server.net;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.kokakiwi.kintell.spec.net.codec.MessageCodec;
import com.kokakiwi.kintell.spec.net.msg.Message;
import com.kokakiwi.kintell.spec.utils.data.DataBuffer;
import com.kokakiwi.kintell.spec.utils.data.DynamicDataBuffer;

public class ServerEncoder extends OneToOneEncoder
{
    private final Server server;
    
    public ServerEncoder(Server server)
    {
        super();
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel,
            Object msg) throws Exception
    {
        if (!(msg instanceof Message))
        {
            return msg;
        }
        
        final Message message = (Message) msg;
        final MessageCodec<Message> codec = (MessageCodec<Message>) server
                .getCodec().getCodec(message.getClass());
        
        if (codec == null)
        {
            throw new NullPointerException("codec");
        }
        
        final DataBuffer buf = new DynamicDataBuffer();
        buf.writeByte(codec.getOpcode());
        codec.encode(buf, message);
        
        buf.copyWritedBytesToReadableBytes();
        
        final ChannelBuffer buffer = ChannelBuffers.buffer(buf
                .getReadableBytesSize() + 4);
        buffer.writeInt(buf.getReadableBytesSize());
        buffer.writeBytes(buf.getReadableBytes());
        
        return buffer;
    }
    
}
