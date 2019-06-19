package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise


import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

class ByteBuffToolsTest extends Specification {
    ByteBuffer data;

    def setup() {
        byte[] arr = new byte[6]
        for (int i = 0; i < arr.length; i++)
            arr[i] = (byte) i
        data = ByteBuffer.wrap(arr)
    }


    def "Test Keeping Position"() {
        when:
        int initialPos = 0
        data.position(initialPos)
        ByteBuffTools.keepPosition(data, { ByteBuffer byteBuffer -> byteBuffer.position(4) })

        then:
        initialPos == data.position()
    }

    def "Test shifting position for consumer"() {
        when:
        int initialPos = 0
        int consumerPositoon = 4
        data.position(initialPos)
        int seenByConsumer = -6
        ByteBuffTools.shiftPosition(data, { ByteBuffer byteBuffer ->
            seenByConsumer = byteBuffer.position()
            byteBuffer.position(1)
        }, consumerPositoon)

        then:
        consumerPositoon == seenByConsumer
        initialPos == data.position()
    }

    def "Test Shifting limit for consumer"() {
        when:
        int initialLimit = data.limit()
        int limitForConsumer = 2
        int seenByConsumer = -6
        ByteBuffTools.shiftLimit(data, { ByteBuffer toEat ->
            seenByConsumer = data.limit()
            data.limit(initialLimit - 2)
        }, limitForConsumer)

        then:
        limitForConsumer == seenByConsumer
        initialLimit == data.limit()
    }

    def "Test nullsafe"() {
        when:
        boolean executed = false
        ByteBuffTools.nullSafeWrapper(buff, { executed = true })
        then:
        executed != isNull
        where:
        buff                         || isNull
        null                         || true
        ByteBuffer.wrap(new byte[0]) || false

    }

    @Unroll
    def "Test lengthFullyToShort"() {
        when:
        def isValid
        try {
            ByteBuffTools.validateFullyTranslatableToShorts(ByteBuffer.wrap(new byte[lengt]))
            isValid = true
        } catch (IllegalBufferLengthException ignored) {
            isValid = false
        }
        then:
        isValid == expected
        where:
        lengt || expected
        0     || true
        2     || true
        4     || true
        1     || false
        3     || false
        5     || false

    }

    @Unroll
    def "Test lengthFullyToInt"() {
        when:
        def isValid
        try {
            ByteBuffTools.validateFullyTranslatableToInts(ByteBuffer.wrap(new byte[lengt]))
            isValid = true
        } catch (IllegalBufferLengthException ignored) {
            isValid = false
        }
        then:
        isValid == expected
        where:
        lengt || expected
        0     || true
        2     || false
        4     || true
        1     || false
        3     || false
        5     || false
        6     || false
        7     || false
        8     || true
    }

}
