package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;

import java.nio.ByteBuffer;

/**
 * Provides methods for executing bitwise operation on all shorts from current position to last short within limit
 * it cuts time by checking condition of while (and executing bitwise operation) 1/4 of time compared to common implementation
 * during execution of methods there is at most 3 ints and one long worth of data occupied
 * class itself occupies 16 bytes by means of constant variables
 */
final public class ShortBulkBitwise implements BulkBitwiseI {
    private final int SHORT_FILTER;
    private final int INT_FILTER;
    private final long LONG_FILTER;

    public ShortBulkBitwise(final int constant) {
        if (constant < 0 || constant > (0b11111111 | (0b11111111 << 8)))
            throw new IllegalArgumentException("Argument (" + constant + ") is  out of range");
        long workValue = constant;
        SHORT_FILTER = constant;
        INT_FILTER = SHORT_FILTER << 16 | SHORT_FILTER;
        workValue |= workValue << 16;
        workValue |= workValue << 32;
        LONG_FILTER = workValue;
    }

    /**
     * Execute bitwise AND operation on All shorts with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    @Override
    final public void AND(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) & LONG_FILTER);

        CommonShortByte.AND(toProcess, lastValidIndex, INT_FILTER, SHORT_FILTER, bufferSize);
    }


    /**
     * Executes bitwise OR operation on All shorts with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    @Override
    final public void OR(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) | LONG_FILTER);

        CommonShortByte.OR(toProcess, bufferSize, lastValidIndex, INT_FILTER, SHORT_FILTER);

    }


    /**
     * Execute bitwise Xor operation on All shorts with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    @Override
    final public void XOR(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) ^ LONG_FILTER);

        CommonShortByte.XOR(toProcess, bufferSize, lastValidIndex, INT_FILTER, SHORT_FILTER);

    }

    /**
     * Execute bitwise NOT operation on All shorts from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    public static void NOT(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(~toProcess.getLong(toProcess.position()));

        CommonShortByte.NOT(toProcess, bufferSize, lastValidIndex);
    }
}
