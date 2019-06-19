package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;

import java.nio.ByteBuffer;

/**
 * Helper class containing static tail calls of short and byte implementations does not inherit bulk bitwise sice this is not ment to be used by users
 */
class CommonShortByte {
    static void AND(ByteBuffer toProcess, int lastValidIndex, int int_filter, int short_filter, int bufferSize) {
        if (toProcess.position() < lastValidIndex) {
            if (toProcess.position() < bufferSize - 3) {
                toProcess.putInt(toProcess.getInt(toProcess.position()) & int_filter);
                if (toProcess.position() >= lastValidIndex)
                    return;
            }
            toProcess.putShort((short) (toProcess.getShort(toProcess.position()) & short_filter));
        }
    }
    static void OR(ByteBuffer toProcess, int bufferSize, int lastValidIndex, int int_filter, int short_filter) {
        if (toProcess.position() < lastValidIndex) {
            if (toProcess.position() < bufferSize - 3) {
                toProcess.putInt(toProcess.getInt(toProcess.position()) | int_filter);
                if (toProcess.position() >= lastValidIndex)
                    return;
            }
            toProcess.putShort((short) (toProcess.getShort(toProcess.position()) | short_filter));
        }
    }
    static void XOR(ByteBuffer toProcess, int bufferSize, int lastValidIndex, int int_filter, int short_filter) {
        if (toProcess.position() < lastValidIndex) {
            if (toProcess.position() < bufferSize - 3) {
                toProcess.putInt(toProcess.getInt(toProcess.position()) ^ int_filter);
                if (toProcess.position() >= lastValidIndex)
                    return;
            }
            toProcess.putShort((short) (toProcess.getShort(toProcess.position()) ^ short_filter));
        }
    }
    static void NOT(ByteBuffer toProcess, int bufferSize, int lastValidIndex) {
        if (toProcess.position() < lastValidIndex) {
            if (toProcess.position() < bufferSize - 3) {
                toProcess.putInt(~toProcess.getInt(toProcess.position()));
                if (toProcess.position() >= lastValidIndex)
                    return;
            }
            toProcess.putShort((short) ~toProcess.getShort(toProcess.position()));
        }
    }
}
