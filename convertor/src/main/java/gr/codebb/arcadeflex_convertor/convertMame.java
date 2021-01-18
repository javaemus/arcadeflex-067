package gr.codebb.arcadeflex_convertor;

import static gr.codebb.arcadeflex_convertor.Convertor.inpos;
import static gr.codebb.arcadeflex_convertor.Convertor.token;
import static gr.codebb.arcadeflex_convertor.sUtil.getToken;
import static gr.codebb.arcadeflex_convertor.sUtil.parseChar;
import static gr.codebb.arcadeflex_convertor.sUtil.parseToken;
import static gr.codebb.arcadeflex_convertor.sUtil.skipLine;
import static gr.codebb.arcadeflex_convertor.sUtil.skipSpace;

public class convertMame {

    public static void ConvertMame() {
        Analyse();
        Convert();
    }

    public static void Analyse() {

    }
    static final int PLOT_BOX = 1;
    static final int PLOT_PIXEL = 2;
    static final int MARK_DIRTY = 3;
    static final int READ_PIXEL = 4;
    static final int MEMORY_READ8 = 5;
    static final int MEMORY_WRITE8 = 6;
    static final int PORT_READ8 = 7;
    static final int PORT_WRITE8 = 8;
    static final int READ_HANDLER8 = 9;
    static final int WRITE_HANDLER8 = 10;
    static final int GFXLAYOUT = 11;
    static final int GFXDECODE = 12;
    static final int ROMDEF = 13;
    static final int GAMEDRIVER = 14;
    static final int NEWINPUT = 15;
    static final int MACHINEDRIVER = 16;
    static final int SN76496 = 17;
    static final int AY8910 = 18;
    static final int MACHINE_INTERRUPT = 19;
    static final int VH_START = 20;
    static final int VH_STOP = 21;
    static final int VH_SCREENREFRESH = 22;
    static final int DRIVER_INIT = 23;
    static final int MACHINE_INIT = 24;
    static final int CUSTOM_SOUND = 25;
    static final int DAC_SOUND = 26;
    static final int VH_CONVERT = 27;
    static final int RECTANGLE = 28;
    static final int POKEY = 29;

    public static void Convert() {
        Convertor.inpos = 0;//position of pointer inside the buffers
        Convertor.outpos = 0;
        boolean only_once_flag = false;//gia na baleis to header mono mia fora
        boolean line_change_flag = false;

        int kapa = 0;
        int i = 0;
        int type = 0;
        int i3 = -1;
        int i8 = -1;
        int type2 = 0;
        int[] insideagk = new int[10];//get the { that are inside functions

        do {
            if (Convertor.inpos >= Convertor.inbuf.length)//an to megethos einai megalitero spase to loop
            {
                break;
            }
            char c = sUtil.getChar(); //pare ton character
            if (line_change_flag) {
                for (int i1 = 0; i1 < kapa; i1++) {
                    sUtil.putString("\t");
                }

                line_change_flag = false;
            }
            switch (c) {
                case 35: // '#'
                {
                    if (!sUtil.getToken("#include"))//an den einai #include min to trexeis
                    {
                        break;
                    }
                    sUtil.skipLine();
                    if (!only_once_flag)//trekse auto to komati mono otan bris to proto include
                    {
                        only_once_flag = true;
                        sUtil.putString("/*\r\n");
                        sUtil.putString(" * ported to v" + Convertor.mameversion + "\r\n");
                        sUtil.putString(" * using automatic conversion tool v" + Convertor.convertorversion + "\r\n");
                        /*sUtil.putString(" * converted at : " + Convertor.timenow() + "\r\n");*/
                        sUtil.putString(" */ \r\n");
                        sUtil.putString("package " + Convertor.packageName + ";\r\n");
                        sUtil.putString("\r\n");
                        sUtil.putString((new StringBuilder()).append("public class ").append(Convertor.className).append("\r\n").toString());
                        sUtil.putString("{\r\n");
                        kapa = 1;
                        line_change_flag = true;
                    }
                    continue;
                }
                case 10: // '\n'
                {
                    Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];
                    line_change_flag = true;
                    continue;
                }
                case 'i': {
                    i = Convertor.inpos;
                    if (getToken("if")) {
                        skipSpace();
                        if (parseChar() != '(') {
                            inpos = i;
                            break;
                        }
                        skipSpace();
                        char c2 = parseChar();
                        if (c2 == '!') {
                            skipSpace();
                            token[0] = parseToken();
                            skipSpace();
                            if (parseChar() != ')') {
                                inpos = i;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("if (").append(token[0]).append(" == 0)").toString());
                            continue;
                        } else {
                            inpos = i;
                            break;
                        }
                    }
                    if (sUtil.getToken("int")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                    }
                    if (sUtil.getToken("void);")) {
                        sUtil.skipLine();
                        continue;
                    }
                    if (sUtil.getToken(" void );")) {
                        sUtil.skipLine();
                        continue;
                    }
                    sUtil.skipSpace();
                    if (sUtil.getToken("void"))//an to soma tis function einai (void)
                    {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (Convertor.token[0].contains("_interrupt")) {
                            sUtil.putString((new StringBuilder()).append("public static InterruptPtr ").append(Convertor.token[0]).append(" = new InterruptPtr() { public int handler() ").toString());
                            type = MACHINE_INTERRUPT;
                            i3 = -1;
                            continue;
                        } else if (Convertor.token[0].contains("_irq")) {
                            sUtil.putString((new StringBuilder()).append("public static InterruptPtr ").append(Convertor.token[0]).append(" = new InterruptPtr() { public int handler() ").toString());
                            type = MACHINE_INTERRUPT;
                            i3 = -1;
                            continue;
                        } else if (Convertor.token[0].contains("vh_start")) {
                            sUtil.putString((new StringBuilder()).append("public static VhStartPtr ").append(Convertor.token[0]).append(" = new VhStartPtr() { public int handler() ").toString());
                            type = VH_START;
                            i3 = -1;
                            continue;
                        }

                    }
                    Convertor.inpos = i;
                    break;
                }

                case 'v': {
                    if (type == WRITE_HANDLER8 || type == VH_SCREENREFRESH || type == VH_START || type == READ_HANDLER8) {
                        if (i3 == -1) {
                            break;//if is not inside a memwrite function break
                        }
                        i = Convertor.inpos;
                        if (sUtil.getToken("videoram_size")) {
                            sUtil.putString((new StringBuilder()).append("videoram_size[0]").toString());
                            continue;
                        }
                    }
                    int j = Convertor.inpos;
                    if (sUtil.getToken("videoram")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = j;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = j;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = j;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("videoram.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("videoram.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    Convertor.inpos = j;
                    if (!sUtil.getToken("void")) {
                        break;
                    }
                    sUtil.skipSpace();
                    Convertor.token[0] = sUtil.parseToken();
                    sUtil.skipSpace();
                    if (sUtil.parseChar() != '(') {
                        Convertor.inpos = j;
                        break;
                    }
                    sUtil.skipSpace();
                    if (sUtil.getToken("struct mame_bitmap *bitmap,int full_refresh")
                            || sUtil.getToken("struct mame_bitmap *bitmap, int full_refresh")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = j;
                            break;
                        }
                        if (sUtil.getChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        }
                        if (Convertor.token[0].contains("vh_screenrefresh")) {
                            sUtil.putString((new StringBuilder()).append("public static VhUpdatePtr ").append(Convertor.token[0]).append(" = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) ").toString());
                            type = VH_SCREENREFRESH;
                            i3 = -1;
                            continue;
                        }

                    }
                    if (sUtil.getToken("struct mame_bitmap *bitmap, int fullrefresh")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = j;
                            break;
                        }
                        if (sUtil.getChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        }
                        if (Convertor.token[0].contains("vh_screenrefresh")) {
                            sUtil.putString((new StringBuilder()).append("public static VhUpdatePtr ").append(Convertor.token[0]).append(" = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int fullrefresh) ").toString());
                            type = VH_SCREENREFRESH;
                            i3 = -1;
                            continue;
                        }

                    }
                    if (sUtil.getToken("unsigned char *palette, unsigned short *colortable,const unsigned char *color_prom")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = j;
                            break;
                        }
                        if (sUtil.getChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        }
                        if (Convertor.token[0].contains("vh_convert_color_prom")) {
                            sUtil.putString((new StringBuilder()).append("public static VhConvertColorPromPtr ").append(Convertor.token[0]).append(" = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) ").toString());
                            type = VH_CONVERT;
                            i3 = -1;
                            continue;
                        }
                        if (Convertor.token[0].contains("init_colors")) {
                            sUtil.putString((new StringBuilder()).append("public static VhConvertColorPromPtr ").append(Convertor.token[0]).append(" = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) ").toString());
                            type = VH_CONVERT;
                            i3 = -1;
                            continue;
                        }
                        if (Convertor.token[0].contains("init_palette")) {
                            sUtil.putString((new StringBuilder()).append("public static VhConvertColorPromPtr ").append(Convertor.token[0]).append(" = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) ").toString());
                            type = VH_CONVERT;
                            i3 = -1;
                            continue;
                        }

                    }
                    if (sUtil.getToken("unsigned char *palette, unsigned short *colortable, const unsigned char *color_prom")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = j;
                            break;
                        }
                        if (sUtil.getChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        }
                        if (Convertor.token[0].contains("vh_convert_color_prom")) {
                            sUtil.putString((new StringBuilder()).append("public static VhConvertColorPromPtr ").append(Convertor.token[0]).append(" = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) ").toString());
                            type = VH_CONVERT;
                            i3 = -1;
                            continue;
                        }
                        if (Convertor.token[0].contains("init_colors")) {
                            sUtil.putString((new StringBuilder()).append("public static VhConvertColorPromPtr ").append(Convertor.token[0]).append(" = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) ").toString());
                            type = VH_CONVERT;
                            i3 = -1;
                            continue;
                        }
                        if (Convertor.token[0].contains("init_palette")) {
                            sUtil.putString((new StringBuilder()).append("public static VhConvertColorPromPtr ").append(Convertor.token[0]).append(" = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) ").toString());
                            type = VH_CONVERT;
                            i3 = -1;
                            continue;
                        }

                    }
                    if (sUtil.getToken("void"))//an to soma tis function einai (void)
                    {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = j;
                            break;
                        }
                        if (sUtil.getChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        }
                        if (Convertor.token[0].contains("vh_stop")) {
                            sUtil.putString((new StringBuilder()).append("public static VhStopPtr ").append(Convertor.token[0]).append(" = new VhStopPtr() { public void handler() ").toString());
                            type = VH_STOP;
                            i3 = -1;
                            continue;
                        } else if (Convertor.token[0].contains("init_machine")) {
                            sUtil.putString((new StringBuilder()).append("public static InitMachinePtr ").append(Convertor.token[0]).append(" = new InitMachinePtr() { public void handler() ").toString());
                            type = MACHINE_INIT;
                            i3 = -1;
                            continue;
                        } else if (Convertor.token[0].contains("machine_init")) {
                            sUtil.putString((new StringBuilder()).append("public static InitMachinePtr ").append(Convertor.token[0]).append(" = new InitMachinePtr() { public void handler() ").toString());
                            type = MACHINE_INIT;
                            i3 = -1;
                            continue;
                        } else if (Convertor.token[0].startsWith("init_") && !Convertor.token[0].contains("table") && !Convertor.token[0].contains("machine") && !Convertor.token[0].contains("palette")) {
                            sUtil.putString((new StringBuilder()).append("public static InitDriverPtr ").append(Convertor.token[0]).append(" = new InitDriverPtr() { public void handler() ").toString());
                            type = DRIVER_INIT;
                            i3 = -1;
                            continue;
                        }
                    }
                    Convertor.inpos = j;
                }
                break;
                case 'p': {
                    int sd = Convertor.inpos;
                    if (sUtil.getToken("paletteram")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = sd;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = sd;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = sd;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("paletteram.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("paletteram.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                }
                break;
                case 'c':
                    int sd = Convertor.inpos;
                    if (sUtil.getToken("color_prom")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = sd;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = sd;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = sd;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("color_prom.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("color_prom.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("colorram")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = sd;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = sd;
                            break;
                        } else {
                            sUtil.skipSpace();

                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = sd;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("colorram.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("colorram.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    break;
                case 'e': {
                    i = Convertor.inpos;
                    if (getToken("enum")) {
                        skipSpace();

                        if (parseChar() != '{') {
                            inpos = i;
                            break;
                        }
                        skipSpace();
                        int i5 = 0;
                        char c2;
                        do {
                            token[i5++] = parseToken();
                            skipSpace();
                            c2 = parseChar();
                            if (c2 != '}' && c2 != ',') {
                                inpos = i;
                                break;
                            }
                            skipSpace();
                        } while (c2 == ',');
                        if (parseChar() != ';') {
                            inpos = i;
                            break;
                        }
                        int k5 = 0;
                        while (k5 < i5) {
                            sUtil.putString((new StringBuilder()).append("public static final int ").append(token[k5]).append(" = ").append(k5).append(";\n\t").toString());
                            k5++;
                        }
                        continue;
                    } else if (getToken("extern")) {
                        skipSpace();
                        if (sUtil.getToken("void")) {
                            skipLine();
                            continue;
                        }
                        if (sUtil.getToken("int")) {
                            skipLine();
                            continue;
                        }
                    }
                    Convertor.inpos = i;
                    break;
                }
                case 's': {
                    if (type == WRITE_HANDLER8 || type == VH_SCREENREFRESH || type == VH_START || type == READ_HANDLER8) {
                        if (i3 == -1) {
                            break;//if is not inside a memwrite function break
                        }
                        i = Convertor.inpos;
                        if (sUtil.getToken("spriteram_size")) {
                            sUtil.putString((new StringBuilder()).append("spriteram_size[0]").toString());
                            continue;
                        }
                    }
                    i = Convertor.inpos;
                    if (sUtil.getToken("spriteram")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = i;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("spriteram.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("spriteram.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("spriteram_2")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = i;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("spriteram_2.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("spriteram_2.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("spriteram_3")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = i;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("spriteram_3.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("spriteram_3.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    i = Convertor.inpos;
                    boolean isstatic = false;
                    if (sUtil.getToken("static")) {
                        sUtil.skipSpace();
                        isstatic = true;
                    }
                    if (!sUtil.getToken("struct")) //static but not static struct
                    {
                        if (sUtil.getToken("int")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            if (sUtil.getToken("void"))//an to soma tis function einai (void)
                            {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ')') {
                                    Convertor.inpos = i;
                                    break;
                                }

                                if (Convertor.token[0].contains("_interrupt")) {
                                    sUtil.putString((new StringBuilder()).append("public static InterruptPtr ").append(Convertor.token[0]).append(" = new InterruptPtr() { public int handler() ").toString());
                                    type = MACHINE_INTERRUPT;
                                    i3 = -1;
                                    continue;
                                }
                                if (Convertor.token[0].contains("_irq")) {
                                    sUtil.putString((new StringBuilder()).append("public static InterruptPtr ").append(Convertor.token[0]).append(" = new InterruptPtr() { public int handler() ").toString());
                                    type = MACHINE_INTERRUPT;
                                    i3 = -1;
                                    continue;
                                }
                            }

                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.getToken("void")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            if (Convertor.token[0].contains("init_machine")) {
                                sUtil.putString((new StringBuilder()).append("public static InitMachinePtr ").append(Convertor.token[0]).append(" = new InitMachinePtr() { public void handler() ").toString());
                                type = MACHINE_INIT;
                                i3 = -1;
                                continue;
                            }
                            if (Convertor.token[0].contains("machine_init")) {
                                sUtil.putString((new StringBuilder()).append("public static InitMachinePtr ").append(Convertor.token[0]).append(" = new InitMachinePtr() { public void handler() ").toString());
                                type = MACHINE_INIT;
                                i3 = -1;
                                continue;
                            }
                            if (Convertor.token[0].startsWith("init_") && !Convertor.token[0].contains("table") && !Convertor.token[0].contains("machine") && !Convertor.token[0].contains("palette")) {
                                sUtil.putString((new StringBuilder()).append("public static InitDriverPtr ").append(Convertor.token[0]).append(" = new InitDriverPtr() { public void handler() ").toString());
                                type = DRIVER_INIT;
                                i3 = -1;
                                continue;
                            }
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            if (sUtil.getToken("struct mame_bitmap *b,int x,int y,int w,int h,UINT32 p")) {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ')') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                if (sUtil.getChar() == ';') {
                                    sUtil.skipLine();
                                    continue;
                                }
                                if (Convertor.token[0].contains("pb_")) {
                                    sUtil.putString((new StringBuilder()).append("public static plot_box_procPtr ").append(Convertor.token[0]).append("  = new plot_box_procPtr() { public void handler(mame_bitmap b, int x, int y, int w, int h, /*UINT32*/int p) ").toString());
                                    type = PLOT_BOX;
                                    i3 = -1;
                                    continue;
                                }

                            }
                            if (sUtil.getToken("struct mame_bitmap *b,int x,int y,UINT32 p")) {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ')') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                if (sUtil.getChar() == ';') {
                                    sUtil.skipLine();
                                    continue;
                                }
                                if (Convertor.token[0].contains("pp_")) {
                                    sUtil.putString((new StringBuilder()).append("public static plot_pixel_procPtr ").append(Convertor.token[0]).append("  = new plot_pixel_procPtr() { public void handler(mame_bitmap b,int x,int y,/*UINT32*/int p) ").toString());
                                    type = PLOT_PIXEL;
                                    i3 = -1;
                                    continue;
                                }

                            }
                            if (sUtil.getToken("int sx,int sy,int ex,int ey")) {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ')') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                if (sUtil.getChar() == ';') {
                                    sUtil.skipLine();
                                    continue;
                                }
                                if (Convertor.token[0].contains("md")) {
                                    sUtil.putString((new StringBuilder()).append("public static mark_dirty_procPtr ").append(Convertor.token[0]).append("  = new mark_dirty_procPtr() { public void handler(int sx,int sy,int ex,int ey) ").toString());
                                    type = MARK_DIRTY;
                                    i3 = -1;
                                    continue;
                                }

                            }
                        } else if (sUtil.getToken("int")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            if (sUtil.getToken("struct mame_bitmap *b,int x,int y")) {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ')') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                if (sUtil.getChar() == ';') {
                                    sUtil.skipLine();
                                    continue;
                                }
                                if (Convertor.token[0].contains("rp_")) {
                                    sUtil.putString((new StringBuilder()).append("public static read_pixel_procPtr ").append(Convertor.token[0]).append("  = new read_pixel_procPtr() { public int handler(mame_bitmap bitmap, int x, int y) ").toString());
                                    type = READ_PIXEL;
                                    i3 = -1;
                                    continue;
                                }

                            }
                        } else if (sUtil.getToken("MEMORY_READ_START(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static Memory_ReadAddress " + Convertor.token[0] + "[]={\n\t\tnew Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),");
                                type = MEMORY_READ8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        } else if (sUtil.getToken("MEMORY_WRITE_START(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static Memory_WriteAddress " + Convertor.token[0] + "[]={\n\t\tnew Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),");
                                type = MEMORY_WRITE8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        } else if (sUtil.getToken("PORT_READ_START(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static IO_ReadPort " + Convertor.token[0] + "[]={\n\t\tnew IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),");
                                type = PORT_READ8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        } else if (sUtil.getToken("PORT_WRITE_START(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static IO_WritePort " + Convertor.token[0] + "[]={\n\t\tnew IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),");
                                type = PORT_WRITE8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        } else if (sUtil.getToken("READ_HANDLER(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(");"))//if it is front function skip it
                            {
                                sUtil.skipLine();
                                continue;
                            } else {
                                sUtil.putString("public static ReadHandlerPtr " + Convertor.token[0] + "  = new ReadHandlerPtr() { public int handler(int offset)");
                                type = READ_HANDLER8;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        } else if (sUtil.getToken("WRITE_HANDLER(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(");"))//if it is a front function skip it
                            {
                                sUtil.skipLine();
                                continue;
                            } else {
                                sUtil.putString("public static WriteHandlerPtr " + Convertor.token[0] + " = new WriteHandlerPtr() {public void handler(int offset, int data)");
                                type = WRITE_HANDLER8;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        } else if (sUtil.getToken("const")) {
                            sUtil.skipSpace();
                            if (sUtil.getToken("struct")) {
                                sUtil.skipSpace();
                                if (sUtil.getToken("MachineDriver")) {
                                    sUtil.skipSpace();
                                    Convertor.token[0] = sUtil.parseToken();
                                    sUtil.skipSpace();
                                    if (sUtil.parseChar() != '=') {
                                        Convertor.inpos = i;
                                    } else {
                                        sUtil.skipSpace();
                                        sUtil.putString("static MachineDriver " + Convertor.token[0] + " = new MachineDriver");
                                        type = MACHINEDRIVER;
                                        i3 = -1;
                                        continue;
                                    }
                                }
                            }
                        }
                        Convertor.inpos = i;
                        break;
                    } else {
                        sUtil.skipSpace();
                        if (isstatic && sUtil.getToken("rectangle")) {//only static struct rectangle conversion
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static rectangle " + Convertor.token[0] + " = new rectangle");
                                type = RECTANGLE;
                                i3 = -1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("GfxLayout")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static GfxLayout " + Convertor.token[0] + " = new GfxLayout");
                                type = GFXLAYOUT;
                                i3 = -1;
                                continue;
                            }
                        } else if (sUtil.getToken("GfxDecodeInfo")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '[') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ']') {
                                    Convertor.inpos = i;
                                } else {
                                    sUtil.skipSpace();
                                    if (sUtil.parseChar() != '=') {
                                        Convertor.inpos = i;
                                    } else {
                                        sUtil.skipSpace();
                                        sUtil.putString("static GfxDecodeInfo " + Convertor.token[0] + "[] =");
                                        type = GFXDECODE;
                                        i3 = -1;
                                        continue;
                                    }
                                }
                            }
                        } else if (sUtil.getToken("SN76496interface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static SN76496interface " + Convertor.token[0] + " = new SN76496interface");
                                type = SN76496;
                                i3 = -1;
                                continue;
                            }
                        } else if (sUtil.getToken("AY8910interface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static AY8910interface " + Convertor.token[0] + " = new AY8910interface");
                                type = AY8910;
                                i3 = -1;
                                continue;
                            }
                        } else if (sUtil.getToken("POKEYinterface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static POKEYinterface " + Convertor.token[0] + " = new POKEYinterface");
                                type = POKEY;
                                i3 = -1;
                                continue;
                            }
                        } else if (sUtil.getToken("CustomSound_interface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static CustomSound_interface " + Convertor.token[0] + " = new CustomSound_interface");
                                type = CUSTOM_SOUND;
                                i3 = -1;
                                continue;
                            }
                        } else if (sUtil.getToken("DACinterface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static DACinterface " + Convertor.token[0] + " = new DACinterface");
                                type = DAC_SOUND;
                                i3 = -1;
                                continue;
                            }
                        }
                        Convertor.inpos = i;
                        break;
                    }
                }
                case '{': {
                    if (type == MEMORY_READ8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new Memory_ReadAddress(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == MEMORY_WRITE8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new Memory_WriteAddress(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == PORT_READ8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new IO_ReadPort(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == PORT_WRITE8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new IO_WritePort(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == MACHINEDRIVER) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = 40;
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && (insideagk[0] == 0)) {
                            sUtil.putString("new MachineCPU[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && (insideagk[0] == 7)) {
                            sUtil.putString("new rectangle(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 8))//case of 1 CPU
                        {
                            sUtil.putString("new rectangle(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 9)) {
                            sUtil.putString("new rectangle(");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && (insideagk[0] == 21)) {
                            sUtil.putString("new MachineSound[] {");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 22)) {
                            sUtil.putString("new MachineSound[] {");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 23)) {
                            sUtil.putString("new MachineSound[] {");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 24)) {
                            sUtil.putString("new MachineSound[] {");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 25)) {
                            sUtil.putString("new MachineSound[] {");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 26)) {
                            sUtil.putString("new MachineSound[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 2) && (insideagk[0] == 0)) {
                            sUtil.putString("new MachineCPU(");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 2) && (insideagk[0] == 21)) {
                            sUtil.putString("new MachineSound(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 2) && (insideagk[0] == 22)) {
                            sUtil.putString("new MachineSound(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 2) && (insideagk[0] == 23)) {
                            sUtil.putString("new MachineSound(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 2) && (insideagk[0] == 24)) {
                            sUtil.putString("new MachineSound(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 2) && (insideagk[0] == 25)) {
                            sUtil.putString("new MachineSound(");
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 2) && (insideagk[0] == 26)) {
                            sUtil.putString("new MachineSound(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == GFXLAYOUT) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 4) || (insideagk[0] == 5) || (insideagk[0] == 6) || (insideagk[0] == 7))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == GFXDECODE) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 1) {
                            sUtil.putString("new GfxDecodeInfo(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == AY8910) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 2))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 3))) {
                            sUtil.putString("new ReadHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 4))) {
                            sUtil.putString("new ReadHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 5))) {
                            sUtil.putString("new WriteHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 6))) {
                            sUtil.putString("new WriteHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                     else if (type == POKEY) {
                            i3++;
                            insideagk[i3] = 0;
                            if (i3 == 0) {
                                Convertor.outbuf[(Convertor.outpos++)] = '(';
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 2))) {
                                sUtil.putString("new int[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 3))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 4))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 5))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 6))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 7))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 8))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 9))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 10))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 11))) {
                                sUtil.putString("new ReadHandlerPtr[] {");
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                    
                    if (type == CUSTOM_SOUND) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == RECTANGLE) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == DAC_SOUND) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == SN76496) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 2))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }

                    if (type == PLOT_PIXEL || type == MARK_DIRTY || type == PLOT_BOX || type == READ_PIXEL
                            || type == READ_HANDLER8 || type == WRITE_HANDLER8 || type == MACHINE_INTERRUPT
                            || type == VH_START || type == VH_STOP || type == VH_SCREENREFRESH || type == DRIVER_INIT || type == MACHINE_INIT || type == VH_CONVERT) {
                        i3++;
                    }
                }
                break;
                case '}': {
                    if ((type == MEMORY_READ8) || type == MEMORY_WRITE8 || type == PORT_READ8 || type == PORT_WRITE8) {
                        i3--;
                        if (i3 == 0) {
                            type = -1;
                        } else if (i3 == 1) {
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == MACHINEDRIVER) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                        if ((i3 == 1) && (insideagk[0] == 0)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && (insideagk[0] == 21)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 22)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 23)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 24)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 25)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 1) && (insideagk[0] == 26)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 0) && (insideagk[0] == 7)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 0) && (insideagk[0] == 8))//for rectangle defination in single cpu only
                        {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        } else if ((i3 == 0) && (insideagk[0] == 9)) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == AY8910) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == POKEY) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == CUSTOM_SOUND) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == RECTANGLE) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == DAC_SOUND) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == SN76496) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == GFXDECODE) {
                        i3--;
                        if (i3 == -1) {
                            type = -1;
                        } else if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == PLOT_PIXEL || type == MARK_DIRTY || type == PLOT_BOX || type == READ_PIXEL
                            || type == READ_HANDLER8 || type == WRITE_HANDLER8 || type == MACHINE_INTERRUPT
                            || type == VH_START || type == VH_STOP || type == VH_SCREENREFRESH || type == DRIVER_INIT || type == MACHINE_INIT || type == VH_CONVERT) {
                        i3--;
                        if (i3 == -1) {
                            sUtil.putString("} };");
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == GFXLAYOUT) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    break;
                }
                case 'M': {
                    i = Convertor.inpos;
                    if (!sUtil.getToken("MEMORY_END")) {
                        Convertor.inpos = i;
                        break;
                    }
                    if (type == MEMORY_READ8) {
                        sUtil.putString("\tnew Memory_ReadAddress(MEMPORT_MARKER, 0)\n\t};");
                        type = -1;
                        Convertor.inpos += 1;
                        continue;
                    } else if (type == MEMORY_WRITE8) {
                        sUtil.putString("\tnew Memory_WriteAddress(MEMPORT_MARKER, 0)\n\t};");
                        type = -1;
                        Convertor.inpos += 1;
                        continue;
                    }
                    Convertor.inpos = i;
                    break;
                }
                case 'P': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("PORT_END")) {
                        if (type == PORT_READ8) {
                            sUtil.putString("\tnew IO_ReadPort(MEMPORT_MARKER, 0)\n\t};");
                            type = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                        if (type == PORT_WRITE8) {
                            sUtil.putString("\tnew IO_WritePort(MEMPORT_MARKER, 0)\n\t};");
                            type = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("PORT_START")) {
                        sUtil.putString((new StringBuilder()).append("PORT_START(); ").toString());
                        continue;
                    }
                    if (sUtil.getToken("PORT_DIPNAME") || sUtil.getToken("PORT_BIT") || sUtil.getToken("PORT_DIPSETTING") || sUtil.getToken("PORT_BITX") || sUtil.getToken("PORT_SERVICE") || sUtil.getToken("PORT_BIT_IMPULSE") || sUtil.getToken("PORT_ANALOG") || sUtil.getToken("PORT_ANALOGX")) {
                        i8++;
                        type2 = NEWINPUT;
                        sUtil.skipSpace();
                        if (sUtil.parseChar() == '(') {
                            Convertor.inpos = i;
                        }
                    }
                    Convertor.inpos = i;
                    break;
                }
                case 'I':
                    int j = Convertor.inpos;
                    if (sUtil.getToken("INPUT_PORTS_START")) {
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = j;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = j;
                            break;
                        }
                        sUtil.putString((new StringBuilder()).append("static InputPortPtr input_ports_").append(Convertor.token[0]).append(" = new InputPortPtr(){ public void handler() { ").toString());
                    }
                    if (sUtil.getToken("INPUT_PORTS_END")) {
                        sUtil.putString((new StringBuilder()).append("INPUT_PORTS_END(); }}; ").toString());
                        continue;
                    }

                    break;
                case '&': {
                    if (type == MEMORY_READ8 || type == MEMORY_WRITE8 || type == PORT_READ8 || type == PORT_WRITE8 || type == GFXDECODE || type == MACHINEDRIVER) {
                        Convertor.inpos += 1;
                        continue;
                    }
                    break;
                }
                case '0':
                    i = Convertor.inpos;
                    if (sUtil.getToken("0")) {
                        Convertor.inpos = i;
                        if (type == MACHINEDRIVER) {
                            if ((i3 == 0) && ((insideagk[i3] == 3) || (insideagk[i3] == 5) || (insideagk[i3] == 6) || (insideagk[i3] == 10) || (insideagk[i3] == 14) || (insideagk[i3] == 15))) {
                                sUtil.putString("null");
                                Convertor.inpos += 1;
                                continue;
                            } else if ((i3 == 0) /*&& (type3==1)*/ && ((insideagk[i3] == 4) || (insideagk[i3] == 8) || (insideagk[i3] == 9) || (insideagk[i3] == 13) || (insideagk[i3] == 14) || (insideagk[i3] == 15) || (insideagk[i3] == 16))) {
                                //case for single core cpus
                                sUtil.putString("null");
                                Convertor.inpos += 1;
                                continue;
                            }
                            if ((i3 == 2) && (insideagk[0] == 0) && ((insideagk[i3] == 4) || (insideagk[i3] == 6))) {
                                sUtil.putString("null");
                                Convertor.inpos += 1;
                            }
                        }
                    }
                    break;
                case ',':
                    if ((type != -1)) {
                        if (i3 != -1) {
                            insideagk[i3] += 1;
                        }
                    }
                    break;
                case 'R': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("ROM_START")) {
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.putString((new StringBuilder()).append("static RomLoadPtr rom_").append(Convertor.token[0]).append(" = new RomLoadPtr(){ public void handler(){ ").toString());
                        continue;
                    }
                    if (sUtil.getToken("ROM_END")) {
                        sUtil.putString((new StringBuilder()).append("ROM_END(); }}; ").toString());
                        continue;
                    }
                    if (sUtil.getToken("ROM_REGION") || sUtil.getToken("ROM_LOAD")
                            || sUtil.getToken("ROM_RELOAD") || sUtil.getToken("ROM_CONTINUE")
                            || sUtil.getToken("ROM_LOAD16_BYTE") || sUtil.getToken("ROM_LOAD_NIB_HIGH")
                            || sUtil.getToken("ROM_LOAD_NIB_LOW") || sUtil.getToken("ROM_FILL")
                            || sUtil.getToken("ROM_COPY") || sUtil.getToken("ROM_LOAD16_WORD")
                            || sUtil.getToken("ROM_LOAD32_BYTE") || sUtil.getToken("ROM_LOAD32_WORD")
                            || sUtil.getToken("ROM_LOAD32_WORD_SWAP") || sUtil.getToken("ROM_REGION16_LE")
                            || sUtil.getToken("ROM_REGION16_BE") || sUtil.getToken("ROM_REGION32_LE") || sUtil.getToken("ROM_REGION32_BE")) {
                        i8++;
                        type2 = ROMDEF;
                        sUtil.skipSpace();
                        if (sUtil.parseChar() == '(') {
                            Convertor.inpos = i;
                        }
                    }
                    if (sUtil.getToken("READ_HANDLER(")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.getToken(");"))//if it is front function skip it
                        {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static ReadHandlerPtr " + Convertor.token[0] + "  = new ReadHandlerPtr() { public int handler(int offset)");
                            type = READ_HANDLER8;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }

                    Convertor.inpos = i;
                }
                break;
                case 'W': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("WRITE_HANDLER(")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.getToken(");"))//if it is a front function skip it
                        {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static WriteHandlerPtr " + Convertor.token[0] + " = new WriteHandlerPtr() {public void handler(int offset, int data)");
                            type = WRITE_HANDLER8;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    Convertor.inpos = i;
                }
                break;
                case ')': {
                    if (type2 == ROMDEF) {
                        i8--;
                        Convertor.outbuf[(Convertor.outpos++)] = ')';
                        Convertor.outbuf[(Convertor.outpos++)] = ';';
                        Convertor.inpos += 2;
                        if (sUtil.getChar() == ')') {//fix for badcrc case
                            Convertor.outpos -= 1;
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.outbuf[(Convertor.outpos++)] = ';';
                            Convertor.inpos += 1;
                        }
                        type2 = -1;
                        continue;
                    }
                    if (type2 == NEWINPUT) {
                        i8--;
                        Convertor.outbuf[(Convertor.outpos++)] = ')';
                        Convertor.outbuf[(Convertor.outpos++)] = ';';
                        Convertor.inpos += 2;
                        if (sUtil.getChar() == ')') {
                            Convertor.inpos += 1;
                        }
                        type2 = -1;
                        continue;
                    }
                }
                break;
                case 'D':
                    if (type2 == NEWINPUT) {
                        i = Convertor.inpos;
                        if (sUtil.getToken("DEF_STR(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.putString((new StringBuilder()).append("DEF_STR( \"").append(Convertor.token[0]).append("\")").toString());
                            i3 = -1;

                            continue;
                        }

                    }
                    break;
                case 'G': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("GAME") || sUtil.getToken("GAMEX")) {
                        sUtil.skipSpace();
                        if (sUtil.getChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            Convertor.inpos += 1;
                        }
                        if (sUtil.getChar() == ')')//fix an issue in driverH
                        {
                            Convertor.inpos = i;
                            break;
                        }
                        type = GAMEDRIVER;
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseTokenGameDriv();//year
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[1] = sUtil.parseToken();//rom
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[2] = sUtil.parseToken();//parent
                        if (Convertor.token[2].matches("0")) {
                            Convertor.token[2] = "null";
                        } else {
                            Convertor.token[2] = "driver_" + Convertor.token[2];
                        }
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[3] = sUtil.parseToken();//machine
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[4] = sUtil.parseToken();//input
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[5] = sUtil.parseToken();//init
                        if (Convertor.token[5].matches("0")) {
                            Convertor.token[5] = "null";
                        } else {
                            Convertor.token[5] = "init_" + Convertor.token[5];
                        }
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[6] = sUtil.parseToken();//ROT
                        //Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[7] = sUtil.parseToken();
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[8] = sUtil.parseToken();//name

                        sUtil.putString((new StringBuilder()).append("public static GameDriver driver_").append(Convertor.token[1]).append("\t   = new GameDriver(\"").append(Convertor.token[0]).append("\"\t,\"").append(Convertor.token[1]).append("\"\t,\"").append(Convertor.className).append(".java\"\t,rom_")
                                .append(Convertor.token[1]).append(",").append(Convertor.token[2])
                                .append("\t,machine_driver_").append(Convertor.token[3])
                                .append("\t,input_ports_").append(Convertor.token[4])
                                .append("\t,").append(Convertor.token[5])
                                .append("\t,").append(Convertor.token[6])
                                .append("\t,").append(Convertor.token[7])
                                .append("\t").append(Convertor.token[8])
                                .toString());
                        continue;
                    }
                }
                break;

            }
            Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];//grapse to inputbuffer sto output
        } while (true);
        if (only_once_flag) {
            sUtil.putString("}\r\n");
        }
    }

}
