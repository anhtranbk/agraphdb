package com.agraph.core.idpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface IdPool {

    long generate();

    static long createWorkerIdentifier() {
        Logger logger = LoggerFactory.getLogger(IdPool.class);
        int machinePiece;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);

                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException ignored) {
                    }
                }
            }
            machinePiece = sb.toString().hashCode();
        } catch (Throwable t) {
            machinePiece = (new SecureRandom()).nextInt();
            logger.warn("Failed to get machine identifier from network interface, " +
                    "using random number instead", t);
        }

        return machinePiece;
    }
}
