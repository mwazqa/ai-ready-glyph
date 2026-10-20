package pl.glyphtoy.aiready;

final class ChatGptSymbol {
    private static final String[] ROWS = {
        "0000000000000000000000000",
        "0000000027996100000000000",
        "00000008fdbbfd44530000000",
        "0000008e50002effffe600000",
        "000003f40005de71028f90000",
        "000009b003cf91000005f5000",
        "0004df805fb2003ba200ac000",
        "005fad807d002afbde706f000",
        "01e90c807d07ef5007ed9f100",
        "07e10c807ede9ed50019fe000",
        "0aa00c807f91019fb3003e900",
        "0b900c807d00000def9104f30",
        "09c00be57d00000d75eb00c90",
        "03f4019fed00000d707c009b0",
        "009e3003bf91019f707c00aa0",
        "000ef91005de9ede707c01e70",
        "001f9de7005fe70d707c08e10",
        "000f507edbf9100d707daf500",
        "000ca0029b3002bf508fd4000",
        "0005f50000018fc300b900000",
        "00009f82017ed50004f300000",
        "000006effffe20005e8000000",
        "00000003544dfbbdf80000000",
        "0000000000016997200000000",
        "0000000000000000000000000"
    };
    static double coverage(int x, int y) { return Character.digit(ROWS[y].charAt(x), 16) / 15.0; }
}
