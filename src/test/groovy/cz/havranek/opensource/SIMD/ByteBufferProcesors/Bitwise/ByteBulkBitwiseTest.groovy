package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class ByteBulkBitwiseTest extends Specification {
    final static int loverCaseConst = 1 << 5

    @Unroll
    def "Test byte AND"() {
        // if you include translation to bytes this is faster than string.toUppercase
        given:
        ByteBulkBitwise executor = new ByteBulkBitwise(95 | 1 << 7)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.AND(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                    || output
        'ACTEG'                  || 'ACTEG'
        'actg'                   || 'ACTG'
        "AcTgactgactgAct"        || input.toUpperCase() //this test all binary filters at once
        "AcTgactgactgActgagagag" || input.toUpperCase() //test if work past it
        "a"                      || "A" //checking byte filter
        "Ac"                     || "AC" //checking short filter
        "AcT"                    || "ACT"//mix of byte AND short filter
    }

    @Unroll
    def "Test ArrayRange"() {
        given:
        int start = 1
        int end = 2
        ByteBulkBitwise executor = new ByteBulkBitwise(95 | 1 << 7)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        ByteBulkBitwise.forArrayPortion(executor.&AND, raw, start, end)
        then:
        output.getBytes() == raw

        where:
        input   || output
        'ACTEG' || 'ACTEG'
        'actg'  || 'aCtg'
        "Ac"    || "AC" //checking short filter
        "AcT"   || "ACT"//mix of byte AND short filter
    }


    @Unroll
    def "Test byte OR"() {
        // if you include translation to bytes this is faster than string.toLowerCase
        given:
        ByteBulkBitwise executor = new ByteBulkBitwise(loverCaseConst)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.OR(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                    || output
        'ACTEG'                  || 'acteg'
        'actg'                   || 'actg'
        "AcTgactgactgAct"        || input.toLowerCase() //this test all binary filters at once
        "AcTgactgactgActgagagag" || input.toLowerCase() //test if work past it
        "A"                      || "a" //checking byte filter
        "AC"                     || "ac" //checking short filter
        "AcT"                    || "act"//mix of byte AND short filter
    }

    @Unroll
    def "Test byte XOR"() {
        //can do caseflipping
        given:
        ByteBulkBitwise executor = new ByteBulkBitwise(loverCaseConst)
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        executor.XOR(ByteBuffer.wrap(raw))
        then:
        output.getBytes() == raw

        where:
        input                    || output
        'ACTEG'                  || 'acteg'
        'actg'                   || 'ACTG'
        "AcTgactgactgAct"        || "aCtGACTGACTGaCT" //this test all binary filters at once
        "AcTgactgactgActgagagag" || "aCtGACTGACTGaCTGAGAGAG"//test if work past it
        "A"                      || "a" //checking byte filter
        "AC"                     || "ac" //checking short filter
        "AcT"                    || "aCt"//mix of byte AND short filter
    }

    def notForAll(ByteBuffer byteBuffer) {
        while (byteBuffer.hasRemaining())
            byteBuffer.put((byte) ~byteBuffer.get(byteBuffer.position()))
    }

    @Unroll
    def "Test byte NOT"() {
        given:
        def UTF8 = StandardCharsets.UTF_8
        when:
        byte[] raw = input.getBytes(UTF8)
        byte[] out = input.getBytes(UTF8)
        notForAll(ByteBuffer.wrap(out))
        ByteBulkBitwise.NOT(ByteBuffer.wrap(raw))

        then:
        out == raw

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
    def "Test byte Ctor"() {
        when:
        boolean inputResult
        try {
            new ByteBulkBitwise(input)
            inputResult = true
        } catch (IllegalArgumentException ignored) {
            inputResult = false
        }
        then:
        isValid == inputResult

        where:
        input      || isValid
        0          || true
        255        || true
        0b11111111 || true
        -1         || false
        256        || false
    }

    @Unroll
    def "test nonzeroLength"() {
        when:
        boolean executed = false
        ByteBulkBitwise.saveZeroLengthWrapper(buff, { ByteBuffer data -> executed = true })
        then:
        isZeroLength != executed
        where:
        buff                                     || isZeroLength
        ByteBuffer.wrap(new byte[0])             || true
        ByteBuffer.wrap(new byte[1])             || false
        ByteBuffer.wrap(new byte[1]).position(1) || true
        ByteBuffer.wrap(new byte[1]).position(0) || false

    }

    @Unroll
    def "Test byte AND nonASCII"(byte[] input, byte[] out) {
        given:

        ByteBulkBitwise executor = new ByteBulkBitwise(loverCaseConst)

        when:

        executor.AND(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [((byte) (-1 & loverCaseConst))]
        [-1, -2]                 || [((byte) (-1 & loverCaseConst)), ((byte) (-2 & loverCaseConst))]
        [-1, -2, -3]             || [((byte) (-1 & loverCaseConst)), ((byte) (-2 & loverCaseConst)), ((byte) (-3 & loverCaseConst))]
        [-1, -2, -3, -4]         || [((byte) (-1 & loverCaseConst)), ((byte) (-2 & loverCaseConst)), ((byte) (-3 & loverCaseConst)), ((byte) (-4 & loverCaseConst))]
        [-1, -2, -3, -4, -5]     || [((byte) (-1 & loverCaseConst)), ((byte) (-2 & loverCaseConst)), ((byte) (-3 & loverCaseConst)), ((byte) (-4 & loverCaseConst)), ((byte) (-5 & loverCaseConst))]
        [-1, -2, -3, -4, -5, -6] || [((byte) (-1 & loverCaseConst)), ((byte) (-2 & loverCaseConst)), ((byte) (-3 & loverCaseConst)), ((byte) (-4 & loverCaseConst)), ((byte) (-5 & loverCaseConst)), ((byte) (-6 & loverCaseConst))]
    }

    @Unroll
    def "Test byte OR nonASCII"(byte[] input, byte[] out) {
        given:

        ByteBulkBitwise executor = new ByteBulkBitwise(loverCaseConst)

        when:

        executor.OR(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [((byte) (-1 | loverCaseConst))]
        [-1, -2]                 || [((byte) (-1 | loverCaseConst)), ((byte) (-2 | loverCaseConst))]
        [-1, -2, -3]             || [((byte) (-1 | loverCaseConst)), ((byte) (-2 | loverCaseConst)), ((byte) (-3 | loverCaseConst))]
        [-1, -2, -3, -4]         || [((byte) (-1 | loverCaseConst)), ((byte) (-2 | loverCaseConst)), ((byte) (-3 | loverCaseConst)), ((byte) (-4 | loverCaseConst))]
        [-1, -2, -3, -4, -5]     || [((byte) (-1 | loverCaseConst)), ((byte) (-2 | loverCaseConst)), ((byte) (-3 | loverCaseConst)), ((byte) (-4 | loverCaseConst)), ((byte) (-5 | loverCaseConst))]
        [-1, -2, -3, -4, -5, -6] || [((byte) (-1 | loverCaseConst)), ((byte) (-2 | loverCaseConst)), ((byte) (-3 | loverCaseConst)), ((byte) (-4 | loverCaseConst)), ((byte) (-5 | loverCaseConst)), ((byte) (-6 | loverCaseConst))]
    }

    @Unroll
    def "Test byte XOR nonASCII"(byte[] input, byte[] out) {
        given:

        ByteBulkBitwise executor = new ByteBulkBitwise(loverCaseConst)

        when:

        executor.XOR(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [((byte) (-1 ^ loverCaseConst))]
        [-1, -2]                 || [((byte) (-1 ^ loverCaseConst)), ((byte) (-2 ^ loverCaseConst))]
        [-1, -2, -3]             || [((byte) (-1 ^ loverCaseConst)), ((byte) (-2 ^ loverCaseConst)), ((byte) (-3 ^ loverCaseConst))]
        [-1, -2, -3, -4]         || [((byte) (-1 ^ loverCaseConst)), ((byte) (-2 ^ loverCaseConst)), ((byte) (-3 ^ loverCaseConst)), ((byte) (-4 ^ loverCaseConst))]
        [-1, -2, -3, -4, -5]     || [((byte) (-1 ^ loverCaseConst)), ((byte) (-2 ^ loverCaseConst)), ((byte) (-3 ^ loverCaseConst)), ((byte) (-4 ^ loverCaseConst)), ((byte) (-5 ^ loverCaseConst))]
        [-1, -2, -3, -4, -5, -6] || [((byte) (-1 ^ loverCaseConst)), ((byte) (-2 ^ loverCaseConst)), ((byte) (-3 ^ loverCaseConst)), ((byte) (-4 ^ loverCaseConst)), ((byte) (-5 ^ loverCaseConst)), ((byte) (-6 ^ loverCaseConst))]
    }
    @Unroll
    def "Test byte NOT nonASCII"(byte[] input, byte[] out) {
        given:

        when:

        ByteBulkBitwise.NOT(ByteBuffer.wrap(input))
        then:
        input == out

        where:
        input                    || out
        [-1]                     || [((byte) ~(-1))]
        [-1, -2]                 || [((byte) ~(-1)), ((byte) ~(-2))]
        [-1, -2, -3]             || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3))]
        [-1, -2, -3, -4]         || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4))]
        [-1, -2, -3, -4, -5]     || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4)), ((byte) ~(-5))]
        [-1, -2, -3, -4, -5, -6] || [((byte) ~(-1)), ((byte) ~(-2)), ((byte) ~(-3)), ((byte) ~(-4)), ((byte) ~(-5)), ((byte) ~(-6))]
    }
}
