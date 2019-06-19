package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;

import java.nio.ByteBuffer;

/**
 * Defines interface for classes implementing in-sito fast bulk binary transformation of ByteBuffers of arbitrary (sometimes nonzero) length
 * aims at classes that speeds up binary translation of ByteBuffers by means of SIMD operations and it limits casting
 * it cuts  time by checking condition of while less time than common implementations (and limit casting where possible
 */
public interface BulkBitwiseI {
    //all classes are expected to have static NOT

    void AND(final ByteBuffer toProcess);
    void OR(final ByteBuffer toProcess);
    void XOR(final ByteBuffer toProcess);
}
