package cz.havranek.opensource.SIMD.ByteBufferProcesors.Bitwise;

final public class IllegalBufferLengthException extends IllegalArgumentException {
    IllegalBufferLengthException(String msg) {
        super(msg);
    }
}