package com.interpark.netty.bytebuffer;

import com.interpark.netty.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class NettyByteBuffer extends LogUtil {
    private static Logger log = LoggerFactory.getLogger(NettyByteBuffer.class);

    public static void main(String[] args) throws Exception {
        NettyByteBuffer n = new NettyByteBuffer();
        n.nio("/small_file");
        n.io("/small_file");
        n.nio("/big_file");
        n.io("/big_file");
    }

    public void nio(String filePath) throws Exception {
        AsynchronousFileChannel fc = null;
        Path path = Paths.get(filePath);

        try {
            fc = AsynchronousFileChannel.open(path,  StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocateDirect((int) fc.size());

            startJob();
            Future<Integer> future = fc.read(buffer, 0);
            while(!future.isDone()) {
                //log.info("아직 읽는중이니까 난 다른일 할꺼야..");
            }

            buffer.flip();
            ByteBuf nettyBuffer = Unpooled.directBuffer((int) fc.size());
            log.info(filePath + "(nio): " + nettyBuffer.toString());
            nettyBuffer.writeBytes(buffer);
            log.info(filePath + "(nio): " + nettyBuffer.toString());

        } catch (Exception e) {
            log.info(catchLog(e));

        } finally {
            if(fc != null) {fc.close();}

            finishJob();
            log.info(filePath + "(nio) " + executeTimeLog());
        }
    }

    public void io(String filePath) throws Exception {
        FileInputStream fis = null;
        Path path = Paths.get(filePath);

        try {
            fis = new FileInputStream(filePath);
            byte[] temp = new byte[fis.available()];
            ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(fis.available());

            log.info(filePath + "(io): " + buffer.toString());
            startJob();

            while((fis.read(temp)) != -1) {
                buffer.writeBytes(temp);
            }

            log.info(filePath + "(io): " + buffer.toString());

        } catch (Exception e) {
            log.error(catchLog(e));

        } finally {
            if(fis != null) {fis.close();}

            finishJob();
            log.info(filePath + "(io) " + executeTimeLog());
        }
    }
}
