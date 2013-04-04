/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import java.nio.ByteBuffer;

/**
 * HexDump.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class HexDump {

    private HexDump() {}

    public static StringBuilder dump(byte[] data) {
        return dump(data, 0, data.length);
    }


    public static StringBuilder dump(byte[] data, int offset, int length) {
        return dump(ByteBuffer.wrap(data, offset, length));
    }


    // dump data from src.0 to src.limit
    public static StringBuilder dump(ByteBuffer data) {
        return dump(data, 0x10000);
    }


    // dump data from src.0 to src.limit
    public static StringBuilder dump(ByteBuffer data, int outputSize) {
        StringBuilder sb = new StringBuilder();
        dump(data, outputSize, sb);
        return sb;
    }


    @SuppressWarnings("SpellCheckingInspection")
    private static final String LAYOUT = /*
             0         1         2         3         4         5         6         7
             01234567890123456789012345678901234567890123456789012345678901234567890123
             XXXX: XX XX XX XX XX XX XX XX - XX XX XX XX XX XX XX XX | ................ */
            "0000:                         -                         | ";


    // dump data from src.0 to src.limit
    @SuppressWarnings("ConstantConditions")
    public static void dump(ByteBuffer src,
                            int outputSize,
                            @SuppressWarnings("SpellCheckingInspection") StringBuilder dest) {

        int total = src.limit();
        if (outputSize > 0x10000) {
            // 最多输出 64k 字节
            outputSize = 0x10000;
        } else if (outputSize < 64) {
            // 最少输出当前位置加前后各 2 行信息
            outputSize = 64;
        }


        // 数据边界：从 boundary1 跳到 boundary2, 再从 nextBoundary1 跳到 nextBoundary2
        int boundary1 = Integer.MAX_VALUE;
        int boundary2 = total;
        int nextBoundary1 = Integer.MAX_VALUE;
        int nextBoundary2 = total;

        // 当数据的实际长度多于允许输出的字节数 (至少多 4 行) 时, 生成数据边界
        if (total >= outputSize + 64) {
            // 保证输出开头 2 行数据, 以及当前位置前 outputSize / 2 个字节（不少于 2 行）
            boundary2 = Math.min(
                    total - outputSize,
                    src.position() - (outputSize >> 1));
            if (boundary2 < 64) { // 至少跳过 2 行数据才有意义
                boundary1 = 0;
                boundary2 = 0;
            } else {
                boundary1 = 32;
                boundary2 &= 0xFFFFFFF0;
            }

            // 保证输出最后 2 行数据, 以及当前位置后 outputSize / 2 个字节（不少于 2 行）
            nextBoundary1 = Math.max(
                    boundary2 + outputSize,
                    src.position() + (outputSize >> 1));
            nextBoundary2 = (total - 32) & 0xFFFFFFF0;
            if (nextBoundary1 > nextBoundary2 - 32) { // 至少跳过 2 行数据才有意义
                nextBoundary1 = Integer.MAX_VALUE;
            } else {
                nextBoundary1 = (nextBoundary1 + 0xF) & 0xFFFFFFF0;
            }
        }

        // 当前位置为 0 时不进行标记
        int currentPosition = src.position() > 0 ? src.position() : -1;
        for (int i = 0; i < total; ) {
            // i = 0, 16, 32, ...
            int pos = dest.length();
            dest.append(LAYOUT);
            writeXXXX(dest, pos, i);

            int end = Math.min(i + 16, total);
            for (int j = 0; i < end; i++, j++) {
                int b = src.get(i) & 0xFF;
                writeXX(dest, pos + ((j < 8) ? 6 : 8) + j * 3, b, (i == currentPosition));
                if ((b >= ' ') && (b < 0x7f)) { // is printable
                    dest.append((char) b);
                } else {
                    dest.append('.');
                }
            }
            dest.append('\n');
            if (i >= boundary1) {
                if (i < boundary2) {
                    dest.append("****: omitting ").append(boundary2 - i).append(" bytes").append('\n');
                    i = boundary2;
                }

                boundary1 = nextBoundary1; // switch to next boundary
                boundary2 = nextBoundary2;
                nextBoundary1 = Integer.MAX_VALUE;
            }
        }
    }


    private static void writeXXXX(StringBuilder sb, int position, int value) {
        String s = Integer.toHexString(value & 0xFFFF);
        // sb.replace(position, position + 4, "0000");
        sb.replace(position + 4 - s.length(), position + 4, s);
    }


    private static void writeXX(StringBuilder sb, int position, int value, boolean markPosition) {
        String s = Integer.toHexString(value & 0xFF);
        sb.setCharAt(position, '0');
        sb.replace(position + 2 - s.length(), position + 2, s);
        if (markPosition) {
            sb.setCharAt(position - 1, '<');
            // sb.setCharAt(position + 2, '>');
        }
    }
}
