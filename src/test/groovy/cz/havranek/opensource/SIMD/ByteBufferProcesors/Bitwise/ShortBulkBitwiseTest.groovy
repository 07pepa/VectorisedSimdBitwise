package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class ShortBulkBitwiseTest extends Specification {

    static final int loverCaseConstShort =  (1 << 5) << 8 | (1 << 5)
    static final int LCCByte =(1 << 5)
    @Unroll
    def "Test AND"() {
        given:

        ShortBulkBitwise executor = new ShortBulkBitwise(((95 | 1 << 7) << 8) | (95 | 1 << 7))
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.AND(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                    || output
        'ACTEg'                  || 'ACTEg'
        'actg'                   || 'ACTG'
        "AcTgactgactgAc"         || input.toUpperCase() //this test all binary filters at once
        "AcTgactgactgActgagagag" || input.toUpperCase() //test if work past it
        "a"                      || "a" // last byte untouched
        "ac"                     || "AC"
        "Act"                    || "ACt"
        ""                       || ""// test zero length
    }


    @Unroll
    def "Test OR"() {
        given:
        ShortBulkBitwise executor = new ShortBulkBitwise(loverCaseConstShort)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.OR(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                    || output
        'ACTEG'                  || 'acteG'
        'actg'                   || 'actg'
        "AcTgactgactgAc"         || input.toLowerCase() //this test all binary filters at once
        "AcTgactgactgActgagagag" || input.toLowerCase() //test if work past it
        "a"                      || "a" // last byte untouched
        "AC"                     || "ac"
        "AcT"                    || "acT"
        ""                       || ""// test zero length
    }

    @Unroll
    def "Test XOR"() {
        //can do caseflipping
        given:
        ShortBulkBitwise executor = new ShortBulkBitwise(loverCaseConstShort)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.XOR(ByteBuffer.wrap(raw))
        then:
        correct.getBytes() == raw

        where:
        input                    || correct
        'ACTEG'                  || 'acteG'
        'actg'                   || 'ACTG'
        "AcTgactgactgAct"        || "aCtGACTGACTGaCt" //this test all binary filters at once
        "AcTgactgactgActgagagag" || "aCtGACTGACTGaCTGAGAGAG"//test if work past it
        "A"                      || "A"
        "AC"                     || "ac"
        "AcT"                    || "aCT"
        ""                       || ""// test zero length
    }

    def notForAll(ByteBuffer byteBuffer) {
        int lastIndex = byteBuffer.limit() - 1
        while (byteBuffer.position() < lastIndex)
            byteBuffer.putShort((short) ~byteBuffer.getShort(byteBuffer.position()))
    }

    @Unroll
    def "Test NOT"() {
        given:
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        byte[] correct = input.getBytes(UTF8)
        notForAll(ByteBuffer.wrap(correct))
        ShortBulkBitwise.NOT(ByteBuffer.wrap(raw))

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
    def "Test short Ctor"() {
        when:
        boolean inputResult
        try {
            new ShortBulkBitwise(input)
            inputResult = true
        } catch (IllegalArgumentException ignored) {
            inputResult = false
        }
        then:
        isValid == inputResult

        where:
        input              || isValid
        -1                 || false
        256                || true
        65536              || false
        65535              || true
        0b1111111111111111 || true
    }


    @Unroll
    def "Test byte AND nonASCII"(byte[] input, byte[] out) {
        given:
        
        ShortBulkBitwise executor = new ShortBulkBitwise(loverCaseConstShort)

        when:

        executor.AND(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [-1]
        [-1, -2]                 || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte))]
        [-1, -2, -3]             || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), -3]
        [-1, -2, -3, -4]         || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), ((byte) (-3 & LCCByte)), ((byte) (-4 & LCCByte))]
        [-1, -2, -3, -4, -5]     || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), ((byte) (-3 & LCCByte)), ((byte) (-4 & LCCByte)), -5]
        [-1, -2, -3, -4, -5, -6] || [((byte) (-1 & LCCByte)), ((byte) (-2 & LCCByte)), ((byte) (-3 & LCCByte)), ((byte) (-4 & LCCByte)), ((byte) (-5 & LCCByte)), ((byte) (-6 & LCCByte))]
    }

    @Unroll
    def "Test Short OR nonASCII"(byte[] input, byte[] out) {
        given:

        ShortBulkBitwise executor = new ShortBulkBitwise(loverCaseConstShort)

        when:

        executor.OR(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [-1]
        [-1, -2]                 || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte))]
        [-1, -2, -3]             || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), -3]
        [-1, -2, -3, -4]         || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), ((byte) (-3 | LCCByte)), ((byte) (-4 | LCCByte))]
        [-1, -2, -3, -4, -5]     || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), ((byte) (-3 | LCCByte)), ((byte) (-4 | LCCByte)), -5]
        [-1, -2, -3, -4, -5, -6] || [((byte) (-1 | LCCByte)), ((byte) (-2 | LCCByte)), ((byte) (-3 | LCCByte)), ((byte) (-4 | LCCByte)), ((byte) (-5 | LCCByte)), ((byte) (-6 | LCCByte))]
    }

    @Unroll
    def "Test byte XOR nonASCII"(byte[] input, byte[] out) {
        given:

        ShortBulkBitwise executor = new ShortBulkBitwise(loverCaseConstShort)

        when:

        executor.XOR(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [-1]
        [-1, -2]                 || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte))]
        [-1, -2, -3]             || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), -3]
        [-1, -2, -3, -4]         || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), ((byte) (-3 ^ LCCByte)), ((byte) (-4 ^ LCCByte))]
        [-1, -2, -3, -4, -5]     || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), ((byte) (-3 ^ LCCByte)), ((byte) (-4 ^ LCCByte)), -5]
        [-1, -2, -3, -4, -5, -6] || [((byte) (-1 ^ LCCByte)), ((byte) (-2 ^ LCCByte)), ((byte) (-3 ^ LCCByte)), ((byte) (-4 ^ LCCByte)), ((byte) (-5 ^ LCCByte)), ((byte) (-6 ^ LCCByte))]
    }

    @Unroll
    def "Test byte NOT nonASCII"(byte[] input, byte[] out) {
        given:

        when:

        ShortBulkBitwise.NOT(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [-1]
        [-1, -2]                 || [((byte) ~(-1)), ((byte) ~(-2))]
        [-1, -2, -3]             || [((byte) ~(-1)), ((byte) ~(-2)), -3]
        [-1, -2, -3, -4]         || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4))]
        [-1, -2, -3, -4, -5]     || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4)), -5]
        [-1, -2, -3, -4, -5, -6] || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4)), ((byte) ~(-5)), ((byte) ~(-6))]
    }
}

