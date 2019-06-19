package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class IntBulkBitwiseTest extends Specification {
    @Unroll
    def "testBitPatternCaster"(int input, long correct) {
        expect:
        IntBulkBitwise.intToLongBitPattern(input) == correct
        where:
        input                               || correct
        //causal values
        2                                   || 2
        0                                   || 0
        1                                   || 1
        Integer.MAX_VALUE - 256             || Integer.MAX_VALUE - 256

        //extremes
        0b11111111111111111111111111111111  || 0b11111111111111111111111111111111L
        0b10000000000000000000000000000000  || 0b10000000000000000000000000000000L
        0b00000000000000000000000000000001  || 0b00000000000000000000000000000001L
        0b10101010101010101010101010101010  || 0b10101010101010101010101010101010L
        0b01010101010101010101010101010101  || 0b01010101010101010101010101010101L

        //out of bounds test
        0b111111111111111111111111111111111 || 0b11111111111111111111111111111111L
        0b110101010101010101010101010101010 || 0b10101010101010101010101010101010L
        0b101010101010101010101010101010101 || 0b01010101010101010101010101010101L
        0b000000000000000000000000000000001 || 0b00000000000000000000000000000001L
        0b100000000000000000000000000000000 || 0L
    }


    static final int loverCaseConstShort = (((1 << 5) << 8 | (1 << 5)) << 16) | (1 << 5) << 8 | (1 << 5)
    static final int LCCByte = (1 << 5)

    @Unroll
    def "Test AND"() {
        given:

        IntBulkBitwise executor = new IntBulkBitwise(((((95 | 1 << 7) << 8) | (95 | 1 << 7)) << 16) | (((95 | 1 << 7) << 8) | (95 | 1 << 7)))
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.AND(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                      || output
        'ACTEg'                    || 'ACTEg'
        'actg'                     || 'ACTG'
        "AcTgactgactgAccc"         || input.toUpperCase() //this test all binary filters at once
        "AcTgactgactgActgagagagff" || input.toUpperCase() //test if work past it
        "a"                        || "a" // last byte untouched
        "ac"                       || "ac"
        "Act"                      || "Act"
        ""                         || ""// test zero length
    }


    @Unroll
    def "Test OR"() {
        given:
        IntBulkBitwise executor = new IntBulkBitwise(loverCaseConstShort)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.OR(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                      || output
        'ACTEG'                    || 'acteG'
        'actg'                     || 'actg'
        "AcTgactgactgAccc"         || input.toLowerCase() //this test all binary filters at once
        "AcTgactgactgActgagagagff" || input.toLowerCase() //test if work past it
        "a"                        || "a" // last byte untouched
        "ac"                       || "ac"
        "acT"                      || "acT"
        ""                         || ""// test zero length
    }

    @Unroll
    def "Test XOR"() {
        //can do caseflipping
        given:
        IntBulkBitwise executor = new IntBulkBitwise(loverCaseConstShort)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.XOR(ByteBuffer.wrap(raw))
        then:
        correct.getBytes() == raw

        where:
        input                      || correct
        'ACTEG'                    || 'acteG'
        'actg'                     || 'ACTG'
        "AcTgactgactgActC"         || "aCtGACTGACTGaCTc" //this test all binary filters at once
        "AcTgactgactgActgagagagCC" || "aCtGACTGACTGaCTGAGAGAGcc"//test if work past it
        "A"                        || "A"
        "AC"                       || "AC"
        "AcT"                      || "AcT"
        ""                         || ""// test zero length
    }

    def notForAll(ByteBuffer byteBuffer) {
        int lastIndex = byteBuffer.limit() - 3
        while (byteBuffer.position() < lastIndex)
        //noinspection GroovyAssignabilityCheck
            byteBuffer.putInt(~byteBuffer.getInt(byteBuffer.position()))
    }

    @Unroll
    def "Test NOT"() {
        given:
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        byte[] correct = input.getBytes(UTF8)
        notForAll(ByteBuffer.wrap(correct))
        IntBulkBitwise.NOT(ByteBuffer.wrap(raw))

        then:
        correct == raw

        where:
        input << [
                'ACTEG',
                'actg',
                "AcTga",
                "AcTgactgactgActgagagag",
                "A",
                "AC",
                "AcT",
        ]
    }

    @Unroll
    def "Test byte AND nonASCII"(byte[] input, byte[] out) {
        given:

        IntBulkBitwise executor = new IntBulkBitwise(loverCaseConstShort)

        when:

        executor.AND(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                            || out
        [-1]                             || [-1]
        [-1, -2]                         || [-1, -2]
        [-1, -2, -3]                     || [-1, -2, -3]
        [-1, -2, -3, -4]                 || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), ((byte) (-3 & LCCByte)), ((byte) (-4 & LCCByte))]
        [-1, -2, -3, -4, -5]             || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), ((byte) (-3 & LCCByte)), ((byte) (-4 & LCCByte)), -5]
        [-1, -2, -3, -4, -5, -6, -7, -8] || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), ((byte) (-3 & LCCByte)), ((byte) (-4 & LCCByte)), ((byte) (-5 & LCCByte)), ((byte) (-6 & LCCByte)), ((byte) (-7 & LCCByte)), ((byte) (-8 & LCCByte))]
    }

    @Unroll
    def "Test Short OR nonASCII"(byte[] input, byte[] out) {
        given:

        IntBulkBitwise executor = new IntBulkBitwise(loverCaseConstShort)

        when:

        executor.OR(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                            || out
        [-1]                             || [-1]
        [-1, -2]                         || [-1, -2]
        [-1, -2, -3]                     || [-1, -2, -3]
        [-1, -2, -3, -4]                 || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), ((byte) (-3 | LCCByte)), ((byte) (-4 | LCCByte))]
        [-1, -2, -3, -4, -5]             || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), ((byte) (-3 | LCCByte)), ((byte) (-4 | LCCByte)), -5]
        [-1, -2, -3, -4, -5, -6, -7, -8] || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), ((byte) (-3 | LCCByte)), ((byte) (-4 | LCCByte)), ((byte) (-5 | LCCByte)), ((byte) (-6 | LCCByte)), ((byte) (-7 | LCCByte)), ((byte) (-8 | LCCByte))]
    }

    @Unroll
    def "Test byte XOR nonASCII"(byte[] input, byte[] out) {
        given:

        IntBulkBitwise executor = new IntBulkBitwise(loverCaseConstShort)

        when:

        executor.XOR(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                            || out
        [-1]                             || [-1]
        [-1, -2]                         || [-1, -2]
        [-1, -2, -3]                     || [-1, -2, -3]
        [-1, -2, -3, -4]                 || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), ((byte) (-3 ^ LCCByte)), ((byte) (-4 ^ LCCByte))]
        [-1, -2, -3, -4, -5]             || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), ((byte) (-3 ^ LCCByte)), ((byte) (-4 ^ LCCByte)), -5]
        [-1, -2, -3, -4, -5, -6, -7, -8] || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), ((byte) (-3 ^ LCCByte)), ((byte) (-4 ^ LCCByte)), ((byte) (-5 ^ LCCByte)), ((byte) (-6 ^ LCCByte)), ((byte) (-7 ^ LCCByte)), ((byte) (-8 ^ LCCByte))]
    }

    @Unroll
    def "Test byte NOT nonASCII"(byte[] input, byte[] out) {
        given:

        when:

        IntBulkBitwise.NOT(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                            || out
        [-1]                             || [-1]
        [-1, -2]                         || [-1, -2]
        [-1, -2, -3]                     || [-1, -2, -3]
        [-1, -2, -3, -4]                 || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4))]
        [-1, -2, -3, -4, -5]             || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4)), -5]
        [-1, -2, -3, -4, -5, -6, -7, -8] || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4)), ((byte) ~(-5)), ((byte) ~(-6)), ((byte) ~(-7)), ((byte) ~(-8))]
    }

}
