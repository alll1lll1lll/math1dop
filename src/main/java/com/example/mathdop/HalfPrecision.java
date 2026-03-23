package com.example.mathdop;


public class HalfPrecision {
    private final float value;

    public HalfPrecision(float value) {
        short float16Bits = floatToHalfFloatBits(value);
        this.value = halfFloatBitsToFloat(float16Bits);
    }

    private short floatToHalfFloatBits(float f) {
        int fbits = Float.floatToIntBits(f);
        //достаю бит знака и сдвигаю
        int sign = (fbits >>> 16) & 0x8000;
        //сдвигаю на размер мантиссы float32
        int exp32 = (fbits >>> 23) & 0xFF;
        //достаю мантиссу
        int mantissa32 = fbits & 0x007FFFFF;

        if (exp32 == 0) {
            return (short) sign;
        }
        if (exp32 == 255) {
            return (short) (sign | 0x7C00 | (mantissa32 != 0 ? 0x0200 : 0));
        }
        //чтобы экспонента была неотрицательнрой нужно смещение для float32 - 127 , а для float16 - 15)))
        int exp16 = exp32 - 127 + 15;

        if (exp16 >= 31) {
            return (short) (sign | 0x7C00);
        }
        if (exp16 <= 0) {
            return (short) sign;
        }
        //у float16 мантисса 10 бит, а у float32 23 битаlsdp[el[dpf[pdf
        int mantissa16 = mantissa32 >>> 13;

        return (short) (sign | (exp16 << 10) | mantissa16);
    }

    private float halfFloatBitsToFloat(short hbits) {
        int sign = (hbits & 0x8000) << 16;
        int exp16 = (hbits & 0x7C00) >>> 10;
        int mantissa16 = hbits & 0x03FF;
        //обработка переполнения/андефлоу
        if (exp16 == 0) {
            return Float.intBitsToFloat(sign);
        }
        if (exp16 == 31) {
            return Float.intBitsToFloat(sign | 0x7F800000 | (mantissa16 != 0 ? 0x00400000 : 0));
        }

        int exp32 = exp16 - 15 + 127;
        int mantissa32 = mantissa16 << 13;

        int fbits = sign | (exp32 << 23) | mantissa32;
        return Float.intBitsToFloat(fbits);
    }

    public HalfPrecision add(HalfPrecision other) {
        return new HalfPrecision(this.value + other.value);
    }

    public HalfPrecision sub(HalfPrecision other) {
        return new HalfPrecision(this.value - other.value);
    }

    public HalfPrecision mul(HalfPrecision other) {
        return new HalfPrecision(this.value * other.value);
    }

    public HalfPrecision div(HalfPrecision other) {
        return new HalfPrecision(this.value / other.value);
    }

    public HalfPrecision abs() {
        return new HalfPrecision(Math.abs(this.value));
    }

    public float get() {
        return this.value;
    }
}