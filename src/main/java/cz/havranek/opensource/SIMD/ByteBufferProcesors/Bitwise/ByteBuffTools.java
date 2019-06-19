package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Class containing convenience wrappers for class methods of Bulk bitwise processor
 */
final public class ByteBuffTools {
    /**
     * stores current position than changes it to new, consumes buffer with consumer then restores original position
     *
     * @param toConsume           non null instance
     * @param consumer            non null instance
     * @param positionForConsumer new position for consumer
     */
    public static void shiftPosition(final ByteBuffer toConsume, final Consumer<ByteBuffer> consumer, final int positionForConsumer) {
        keepPosition(toConsume, (in) -> {
            in.position(positionForConsumer);
            consumer.accept(in);
        });
    }

    /**
     * stores current position consumes buffer with consumer then restores original position
     *
     * @param toConsume non null instance
     * @param consumer  non null instance
     */
    public static void keepPosition(ByteBuffer toConsume, Consumer<ByteBuffer> consumer) {
        int position = toConsume.position();
        consumer.accept(toConsume);
        toConsume.position(position);
    }

    /**
     * stores current limit than changes it to new, consumes buffer with consumer then restores original limit
     *
     * @param toConsume        non null instance
     * @param consumer         non null instance
     * @param limitForConsumer new limit for consumer
     */
    public static void shiftLimit(ByteBuffer toConsume, Consumer<ByteBuffer> consumer, int limitForConsumer) {
        int limit = toConsume.limit();
        toConsume.limit(limitForConsumer);
        consumer.accept(toConsume);
        toConsume.limit(limit);
    }


    /**
     * validates if buffer is fully translatable to shorts
     *
     * @param toCheck non null instance
     * @throws IllegalBufferLengthException if is not fully translatable
     */
    static void validateFullyTranslatableToShorts(ByteBuffer toCheck) throws IllegalBufferLengthException {
        if (!isFullyTranslatableToShorts(toCheck))
            throw new IllegalBufferLengthException("ByteBuffer is not fully  translatable to short, some bytes would dangle at end");
    }

    /**
     * Convenience method that check if whole buffer can be procesed by shorts and no untranslated bits at end would be dangling
     *
     * @param toCheck non null instance
     * @return -- true if whole buffer can be translated to shorts without dangling bits (no translation (zero length is valid also)
     * -- false if some bytes would be dangering
     */
    static boolean isFullyTranslatableToShorts(ByteBuffer toCheck) {
        return toCheck.limit() % 2 == 0;
    }


    /**
     * validates if buffer is fully translatable to ints (even zero ints translation is valid)
     *
     * @param toCheck non null instance
     * @throws IllegalBufferLengthException if is not fully translatable
     */
    static void validateFullyTranslatableToInts(ByteBuffer toCheck) throws IllegalBufferLengthException {
        if (!isFullyTranslatableToInts(toCheck))
            throw new IllegalBufferLengthException("ByteBuffer is not fully  translatable to int, some bytes would dangle at end");
    }

    /**
     * Convenience method that check if whole buffer can be procesed by ints and no untranslated bits at end would be dangling
     *
     * @param toCheck non null instance
     * @return -- true if whole buffer can be translated to ints without dangling bit (even zero ints is valid)
     * -- false if some bytes would be dangering
     */
    static boolean isFullyTranslatableToInts(ByteBuffer toCheck) {
        return toCheck.limit() % 4 == 0;
    }

    /**
     * for non null save consumer execute if instance is not null
     * @param in any byteBuffer
     * @param nullUnsafeConsumer non null instance of consumer executed if in is not null
     */
    static void nullSafeWrapper(ByteBuffer in,Consumer<ByteBuffer> nullUnsafeConsumer){
        if(in!=null)
            nullUnsafeConsumer.accept(in);
    }
}
