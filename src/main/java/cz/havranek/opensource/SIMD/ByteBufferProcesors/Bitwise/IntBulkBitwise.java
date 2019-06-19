package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;

import java.nio.ByteBuffer;

/**
 * Provides methods for executing bitwise operation on all ints from current position to last short within limit
 * it cuts time by checking condition of while (and executing bitwise operation) 1/2 of time compared to common implementation
 * during execution of methods there is at most 2 ints and one long worth of data occupied
 * class itself occupies 12 bytes by means of constant variables
 */
final public class IntBulkBitwise implements BulkBitwiseI {
    private final int INT_FILTER;
    private final long LONG_FILTER;


    static private long intToLongBitPattern(int in) {
        final long nativeTransformed = (long) in;

        final long extractMask = 0b1000000000000000000000000000000000000000000000000000000000000000L;

        final long singMask = 0b11111111111111111111111111111111L;
        return (((nativeTransformed & extractMask) >>> 32)| nativeTransformed) & singMask;
    }

    /**
     * Construct class for bitwise operation
     * @param constant constant to use... it is highly recomended to input it in 0b101010 form
     */
    public IntBulkBitwise(final int constant) {

        long workValue = intToLongBitPattern(constant);
        INT_FILTER = constant;
        workValue |= workValue << 32;
        LONG_FILTER = workValue;
    }

    /**
     * Execute bitwise AND operation on All ints with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    @Override
    final public void AND(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) & LONG_FILTER);

        if (toProcess.position() < bufferSize - 3)
            toProcess.putInt(toProcess.getInt(toProcess.position()) & INT_FILTER);
    }

    /**
     * Executes bitwise OR operation on All ints with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    @Override
    final public void OR(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) | LONG_FILTER);

        if (toProcess.position() < bufferSize - 3)
            toProcess.putInt(toProcess.getInt(toProcess.position()) | INT_FILTER);
    }

    /**
     * Execute bitwise Xor operation on All ints with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    @Override
    final public void XOR(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) ^ LONG_FILTER);

        if (toProcess.position() < bufferSize - 3)
            toProcess.putInt(toProcess.getInt(toProcess.position()) ^ INT_FILTER);
    }

    /**
     * Execute bitwise NOT operation on All ints from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess writable non null instance
     */

    public static void NOT(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(~toProcess.getLong(toProcess.position()));

        if (toProcess.position() < bufferSize - 3)
            toProcess.putInt(~toProcess.getInt(toProcess.position()));

    }
}