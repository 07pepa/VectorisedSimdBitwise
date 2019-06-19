package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;


import java.nio.ByteBuffer;
import java.util.function.Consumer;


/**
 * Performance focused Bitwise operation provider on nonzero length ByteBuffers and byte arrays
 * it cuts time by checking condition of while (and executing bitwise operation) 1/8 of time compared to common implementation
 * during execution of methods there is at most 3 ints and one long worth of data occupied
 * class itself occupies 20 bytes by means of constant variables
 * <p>
 * nonzero length mentioned in methods documentation means from curent position to limit there must be at least one byte
 */
final public class ByteBulkBitwise implements BulkBitwiseI {
    private final int BYTE_FILTER;
    private final int SHORT_FILTER;
    private final int INT_FILTER;
    private final long LONG_FILTER;

    /**
     * Construct class for bitwise operation
     * @param constant constant to use... it is highly recommended to input it in 0b101010 form
     * @throws IllegalArgumentException if argument can't be mapped to int ()
     */
    public ByteBulkBitwise(final int constant) {
        if (constant < 0 || constant > 0b11111111)
            throw new IllegalArgumentException("Argument (" + constant + ") is  out of range");
        BYTE_FILTER = constant;
        long workValue = constant;
        SHORT_FILTER = BYTE_FILTER << 8 | BYTE_FILTER;
        workValue |= workValue << 8;
        INT_FILTER = SHORT_FILTER << 16 | SHORT_FILTER;
        workValue |= workValue << 16;
        workValue |= workValue << 32;
        LONG_FILTER = workValue;
    }

    /**
     * Execute bitwise AND operation on All bytes with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess nonzero length, writable non null instance
     */

    @Override
    final public void AND(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) & LONG_FILTER);

        //take care of odd bit
        toProcess.put(lastValidIndex, (byte) (toProcess.get(lastValidIndex) & BYTE_FILTER));


        CommonShortByte.AND(toProcess, lastValidIndex, INT_FILTER, SHORT_FILTER, bufferSize);
    }

    /**
     * Executes bitwise OR operation on All bytes with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess nonzero length, writable non null instance
     */

    @Override
    final public void OR(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) | LONG_FILTER);

        //take care of odd bit
        toProcess.put(lastValidIndex, (byte) (toProcess.get(lastValidIndex) | BYTE_FILTER));


        CommonShortByte.OR(toProcess, bufferSize, lastValidIndex, INT_FILTER, SHORT_FILTER);

    }

    /**
     * Execute bitwise Xor operation on All bytes with constant specified in constructor from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess nonzero length, writable non null instance
     */

    @Override
    final public void XOR(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(toProcess.getLong(toProcess.position()) ^ LONG_FILTER);

        //take care of odd bit
        if (toProcess.limit() % 2 == 1)
            toProcess.put(lastValidIndex, (byte) (toProcess.get(lastValidIndex) ^ BYTE_FILTER));


        CommonShortByte.XOR(toProcess, bufferSize, lastValidIndex, INT_FILTER, SHORT_FILTER);

    }


    /**
     * Execute bitwise NOT operation on All bytes from current position of buffer to it's limit
     * position is affected during execution
     * is thread save if nothing else is modifying(reading data) of buffer
     *
     * @param toProcess nonzero length, writable non null instance
     */
    public static void NOT(ByteBuffer toProcess) {
        final int bufferSize = toProcess.limit();
        final int lastValidIndex = bufferSize - 1;
        final int lastLongIndex = bufferSize - 7;

        while (toProcess.position() < lastLongIndex)
            toProcess.putLong(~toProcess.getLong(toProcess.position()));

        //take care of odd bit
        if (toProcess.limit() % 2 == 1)
            toProcess.put(lastValidIndex, (byte) ~toProcess.get(lastValidIndex));


        CommonShortByte.NOT(toProcess, bufferSize, lastValidIndex);
    }


    /**
     * Convenience method for executing on portion of array
     *
     * @param operation operation to execute upon array
     * @param array     the array
     * @param start     inclusive start of portion
     * @param end       noninclusive end of portion
     */
    static void forArrayPortion(Consumer<ByteBuffer> operation, byte[] array, int start, int end) {
        final ByteBuffer wrapped = ByteBuffer.wrap(array);
        wrapped.limit(end);
        wrapped.position(start);
        operation.accept(wrapped);
    }

    /**
     * convenience method for wrapping zero length unsafe operation
     *
     * @param in                    non null instance
     * @param zeroLengthUnsafeTrans executed if length is nonzero
     */
    static void saveZeroLengthWrapper(ByteBuffer in, Consumer<ByteBuffer> zeroLengthUnsafeTrans) {
        if (in.limit() > in.position())
            zeroLengthUnsafeTrans.accept(in);
    }
}
