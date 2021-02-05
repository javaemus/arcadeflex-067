package gr.codebb.arcadeflex.v067.platform;

import static gr.codebb.arcadeflex.v067.platform.rcH.*;
import static gr.codebb.arcadeflex.common.libc.cstdio.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.v067.mame.common.*;
import gr.codebb.arcadeflex.v067.mame.cpuexecH.MachineCPU;
import static gr.codebb.arcadeflex.v067.mame.cpuintrf.cputype_name;
import static gr.codebb.arcadeflex.v067.mame.driver.drivers;
import static gr.codebb.arcadeflex.v067.mame.version.build_version;
import static gr.codebb.arcadeflex.v067.mame.driverH.*;
import static gr.codebb.arcadeflex.v067.mame.mame.expand_machine_driver;

public class fronthlp {

    public static final int LIST_SHORT = 1;
    public static final int LIST_INFO = 2;
    public static final int LIST_FULL = 3;
    public static final int LIST_SAMDIR = 4;
    public static final int LIST_ROMS = 5;
    public static final int LIST_SAMPLES = 6;
    public static final int LIST_LMR = 7;
    public static final int LIST_DETAILS = 8;
    public static final int LIST_GAMELIST = 9;
    public static final int LIST_GAMES = 10;
    public static final int LIST_CLONES = 11;
    public static final int LIST_WRONGORIENTATION = 12;
    public static final int LIST_WRONGFPS = 13;
    public static final int LIST_CRC = 14;
    public static final int LIST_DUPCRC = 15;
    public static final int LIST_WRONGMERGE = 16;
    public static final int LIST_ROMSIZE = 17;
    public static final int LIST_ROMDISTRIBUTION = 18;
    public static final int LIST_ROMNUMBER = 19;
    public static final int LIST_PALETTESIZE = 20;
    public static final int LIST_CPU = 21;
    public static final int LIST_CPUCLASS = 22;
    public static final int LIST_NOSOUND = 23;
    public static final int LIST_SOUND = 24;
    public static final int LIST_NVRAM = 25;
    public static final int LIST_SOURCEFILE = 26;
    public static final int LIST_GAMESPERSOURCEFILE = 27;
//TODO #else
//TODO #include "messwin.h"
//TODO enum { LIST_SHORT = 1, LIST_INFO, LIST_FULL, LIST_SAMDIR, LIST_ROMS, LIST_SAMPLES,
//TODO 		LIST_LMR, LIST_DETAILS, LIST_GAMELIST,
//TODO 		LIST_GAMES, LIST_CLONES,
//TODO 		LIST_WRONGORIENTATION, LIST_WRONGFPS, LIST_CRC, LIST_DUPCRC, LIST_WRONGMERGE,
//TODO 		LIST_ROMSIZE, LIST_ROMDISTRIBUTION, LIST_ROMNUMBER, LIST_PALETTESIZE,
//TODO 		LIST_CPU, LIST_CPUCLASS, LIST_NOSOUND, LIST_SOUND, LIST_NVRAM, LIST_SOURCEFILE,
//TODO 		LIST_GAMESPERSOURCEFILE, LIST_MESSTEXT, LIST_MESSDEVICES, LIST_MESSCREATEDIR };
//TODO #endif
//TODO 
//TODO #define VERIFY_ROMS		0x00000001
//TODO #define VERIFY_SAMPLES	0x00000002
//TODO #define VERIFY_VERBOSE	0x00000004
//TODO #define VERIFY_TERSE	0x00000008
//TODO 
//TODO #define KNOWN_START 0
//TODO #define KNOWN_ALL   1
//TODO #define KNOWN_NONE  2
//TODO #define KNOWN_SOME  3
//TODO 
//TODO #ifndef MESS
//TODO #define YEAR_BEGIN 1975
//TODO #define YEAR_END   2000
//TODO #else
//TODO #define YEAR_BEGIN 1950
//TODO #define YEAR_END   2000
//TODO #endif

    static int list = 0;
    static int listclones = 1;
    static int verify = 0;
    static int ident = 0;
    static int help = 0;
//TODO static int sortby = 0;

    static rc_assign_func assign_list = new rc_assign_func() {
        @Override
        public void handler(int value) {
            list = value;
        }
    };

    static rc_assign_func assign_help = new rc_assign_func() {
        @Override
        public void handler(int value) {
            help = value;
        }
    };

    public static rc_option frontend_opts[] = {
        new rc_option("Frontend Related", null, rc_seperator, null, null, 0, 0, null, null),
        new rc_option("help", "h", rc_set_int, assign_help, null, 1, 0, null, "show help message"),
        new rc_option("?", null, rc_set_int, assign_help, null, 1, 0, null, "show help message"),
        /* list options follow */
        new rc_option("list", "ls", rc_set_int, assign_list, null, LIST_SHORT, 0, null, "List supported games matching gamename, or all, gamename may contain * and ? wildcards"),
        new rc_option("listfull", "ll", rc_set_int, assign_list, null, LIST_FULL, 0, null, "short name, full name"),
        new rc_option("listgames", null, rc_set_int, assign_list, null, LIST_GAMES, 0, null, "year, manufacturer and full name"),
        new rc_option("listdetails", null, rc_set_int, assign_list, null, LIST_DETAILS, 0, null, "detailed info"),
        new rc_option("gamelist", null, rc_set_int, assign_list, null, LIST_GAMELIST, 0, null, "output gamelist.txt main body"),
        new rc_option("listsourcefile", null, rc_set_int, assign_list, null, LIST_SOURCEFILE, 0, null, "driver sourcefile"),
        //TODO 	{ "listgamespersourcefile",	null, rc_set_int, &list, null, LIST_GAMESPERSOURCEFILE, 0, null, "games per sourcefile" },
        //TODO 	{ "listinfo", "li", rc_set_int, &list, null, LIST_INFO, 0, null, "all available info on driver" },
        //TODO 	{ "listclones", "lc", rc_set_int, &list, null, LIST_CLONES, 0, null, "show clones" },
        //TODO 	{ "listsamdir", null, rc_set_int, &list, null, LIST_SAMDIR, 0, null, "shared sample directory" },
        //TODO 	{ "listcrc", null, rc_set_int, &list, null, LIST_CRC, 0, null, "checksums" },
        //TODO 	{ "listdupcrc", null, rc_set_int, &list, null, LIST_DUPCRC, 0, null, "duplicate crc's" },
        //TODO 	{ "listwrongmerge", "lwm", rc_set_int, &list, null, LIST_WRONGMERGE, 0, null, "wrong merge attempts" },
        //TODO 	{ "listromsize", null, rc_set_int, &list, null, LIST_ROMSIZE, 0, null, "rom size" },
        //TODO 	{ "listromdistribution", null, rc_set_int, &list, null, LIST_ROMDISTRIBUTION, 0, null, "rom distribution" },
        //TODO 	{ "listromnumber", null, rc_set_int, &list, null, LIST_ROMNUMBER, 0, null, "rom size" },
        //TODO 	{ "listpalettesize", "lps", rc_set_int, &list, null, LIST_PALETTESIZE, 0, null, "palette size" },
        //TODO 	{ "listcpu", null, rc_set_int, &list, null, LIST_CPU, 0, null, "cpu's used" },
        //TODO 	{ "listcpuclass", null, rc_set_int, &list, null, LIST_CPUCLASS, 0, null, "class of cpu's used by year" },
        //TODO 	{ "listnosound", null, rc_set_int, &list, null, LIST_NOSOUND, 0, null, "drivers missing sound support" },
        //TODO 	{ "listsound", null, rc_set_int, &list, null, LIST_SOUND, 0, null, "sound chips used" },
        //TODO 	{ "listnvram",	null, rc_set_int, &list, null, LIST_NVRAM, 0, null, "games with nvram" },
        //TODO #ifdef MAME_DEBUG /* do not put this into a public release! */
        //TODO 	{ "lmr", null, rc_set_int, &list, null, LIST_LMR, 0, null, "missing roms" },
        //TODO #endif
        //TODO 	{ "wrongorientation", null, rc_set_int, &list, null, LIST_WRONGORIENTATION, 0, null, "wrong orientation" },
        //TODO 	{ "wrongfps", null, rc_set_int, &list, null, LIST_WRONGFPS, 0, null, "wrong fps" },
        //TODO 	{ "clones", null, rc_bool, &listclones, "1", 0, 0, null, "enable/disable clones" },
        //TODO #ifdef MESS
        //TODO 	{ "listdevices", null, rc_set_int, &list, null, LIST_MESSDEVICES, 0, null, "list available devices" },
        //TODO 	{ "listtext", null, rc_set_int, &list, null, LIST_MESSTEXT, 0, null, "list available file extensions" },
        //TODO 	{ "createdir", null, rc_set_int, &list, null, LIST_MESSCREATEDIR, 0, null, null },
        //TODO #endif
        new rc_option("listroms", null, rc_set_int, assign_list, null, LIST_ROMS, 0, null, "list required roms for a driver"),
        //TODO 	{ "listsamples", null, rc_set_int, &list, null, LIST_SAMPLES, 0, null, "list optional samples for a driver" },
        //TODO 	{ "verifyroms", null, rc_set_int, &verify, null, VERIFY_ROMS, 0, null, "report romsets that have problems" },
        //TODO 	{ "verifysets", null, rc_set_int, &verify, null, VERIFY_ROMS|VERIFY_VERBOSE|VERIFY_TERSE, 0, null, "verify checksums of romsets (terse)" },
        //TODO 	{ "vset", null, rc_set_int, &verify, null, VERIFY_ROMS|VERIFY_VERBOSE, 0, null, "verify checksums of a romset (verbose)" },
        //TODO 	{ "verifysamples", null, rc_set_int, &verify, null, VERIFY_SAMPLES|VERIFY_VERBOSE, 0, null, "report samplesets that have problems" },
        //TODO 	{ "vsam", null, rc_set_int, &verify, null, VERIFY_SAMPLES|VERIFY_VERBOSE, 0, null, "verify a sampleset" },
        //TODO 	{ "romident", null, rc_set_int, &ident, null, 1, 0, null, "compare files with known MAME roms" },
        //TODO 	{ "isknown", null, rc_set_int, &ident, null, 2, 0, null, "compare files with known MAME roms (brief)" },
        //TODO 	{ "sortname", null, rc_set_int, &sortby, null, 1, 0, null, "sort by descriptive name" },
        //TODO 	{ "sortdriver", null, rc_set_int, &sortby, null, 2, 0, null, "sort by driver" },
        new rc_option(null, null, rc_end, null, null, 0, 0, null, null)
    };

//TODO int silentident,knownstatus;
//TODO 
//TODO extern unsigned int crc32 (unsigned int crc, const unsigned char *buf, unsigned int len);
//TODO 
//TODO void get_rom_sample_path (int argc, char **argv, int game_index, char *override_default_rompath);
//TODO 
    static GameDriver gamedrv;
//TODO 
//TODO /* compare string[8] using standard(?) DOS wildchars ('?' & '*')      */
//TODO /* for this to work correctly, the shells internal wildcard expansion */
//TODO /* mechanism has to be disabled. Look into msdos.c */
//TODO 
//TODO static int strwildcmp(const char *sp1, const char *sp2)
//TODO {
//TODO 	char s1[9], s2[9];
//TODO 	int i, l1, l2;
//TODO 	char *p;
//TODO 
//TODO 	strncpy(s1, sp1, 8); s1[8] = 0; if (s1[0] == 0) strcpy(s1, "*");
//TODO 
//TODO 	strncpy(s2, sp2, 8); s2[8] = 0; if (s2[0] == 0) strcpy(s2, "*");
//TODO 
//TODO 	p = strchr(s1, '*');
//TODO 	if (p)
//TODO 	{
//TODO 		for (i = p - s1; i < 8; i++) s1[i] = '?';
//TODO 		s1[8] = 0;
//TODO 	}
//TODO 
//TODO 	p = strchr(s2, '*');
//TODO 	if (p)
//TODO 	{
//TODO 		for (i = p - s2; i < 8; i++) s2[i] = '?';
//TODO 		s2[8] = 0;
//TODO 	}
//TODO 
//TODO 	l1 = strlen(s1);
//TODO 	if (l1 < 8)
//TODO 	{
//TODO 		for (i = l1 + 1; i < 8; i++) s1[i] = ' ';
//TODO 		s1[8] = 0;
//TODO 	}
//TODO 
//TODO 	l2 = strlen(s2);
//TODO 	if (l2 < 8)
//TODO 	{
//TODO 		for (i = l2 + 1; i < 8; i++) s2[i] = ' ';
//TODO 		s2[8] = 0;
//TODO 	}
//TODO 
//TODO 	for (i = 0; i < 8; i++)
//TODO 	{
//TODO 		if (s1[i] == '?' && s2[i] != '?') s1[i] = s2[i];
//TODO 		if (s2[i] == '?' && s1[i] != '?') s2[i] = s1[i];
//TODO 	}
//TODO 
//TODO 	return stricmp(s1, s2);
//TODO }

    static String namecopy(String name_ref, String desc) {
        String name = "";

        name = desc;

        /* remove details in parenthesis */
        if (strstr(name, " (") != -1) {
            name = name.substring(0, strstr(name, " ("));
        }

        /* Move leading "The" to the end */
        if (strncmp(name.toCharArray(), "The ", 4) == false) {
            name_ref = sprintf("%s, The", name + 4);
        } else {
            name_ref = sprintf("%s", name);
        }

        return name_ref;
    }

//TODO /* Identifies a rom from from this checksum */
//TODO static void match_roms(const struct GameDriver *driver,int checksum,int *found)
//TODO {
//TODO 	const struct RomModule *region, *rom;
//TODO 
//TODO 	for (region = rom_first_region(driver); region; region = rom_next_region(region))
//TODO 	{
//TODO 		for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 		{
//TODO 			if (checksum == ROM_GETCRC(rom))
//TODO 			{
//TODO 				if (!silentident)
//TODO 				{
//TODO 					if (*found != 0)
//TODO 						printf("             ");
//TODO 					printf("= %-12s  %s\n",ROM_GETNAME(rom),driver->description);
//TODO 				}
//TODO 				(*found)++;
//TODO 			}
//TODO 			if (BADCRC(checksum) == ROM_GETCRC(rom))
//TODO 			{
//TODO 				if (!silentident)
//TODO 				{
//TODO 					if (*found != 0)
//TODO 						printf("             ");
//TODO 					printf("= (BAD) %-12s  %s\n",ROM_GETNAME(rom),driver->description);
//TODO 				}
//TODO 				(*found)++;
//TODO 			}
//TODO 		}
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO void identify_rom(const char* name, int checksum, int length)
//TODO {
//TODO 	int found = 0;
//TODO 
//TODO 	/* remove directory name */
//TODO 	int i;
//TODO 	for (i = strlen(name)-1;i >= 0;i--)
//TODO 	{
//TODO 		if (name[i] == '/' || name[i] == '\\')
//TODO 		{
//TODO 			i++;
//TODO 			break;
//TODO 		}
//TODO 	}
//TODO 	if (!silentident)
//TODO 		printf("%s ",&name[0]);
//TODO 
//TODO 	for (i = 0; drivers[i]; i++)
//TODO 		match_roms(drivers[i],checksum,&found);
//TODO 
//TODO 	for (i = 0; test_drivers[i]; i++)
//TODO 		match_roms(test_drivers[i],checksum,&found);
//TODO 
//TODO 	if (found == 0)
//TODO 	{
//TODO 		unsigned size = length;
//TODO 		while (size && (size & 1) == 0) size >>= 1;
//TODO 		if (size & ~1)
//TODO 		{
//TODO 			if (!silentident)
//TODO 				printf("NOT A ROM\n");
//TODO 		}
//TODO 		else
//TODO 		{
//TODO 			if (!silentident)
//TODO 				printf("NO MATCH\n");
//TODO 			if (knownstatus == KNOWN_START)
//TODO 				knownstatus = KNOWN_NONE;
//TODO 			else if (knownstatus == KNOWN_ALL)
//TODO 				knownstatus = KNOWN_SOME;
//TODO 		}
//TODO 	}
//TODO 	else
//TODO 	{
//TODO 		if (knownstatus == KNOWN_START)
//TODO 			knownstatus = KNOWN_ALL;
//TODO 		else if (knownstatus == KNOWN_NONE)
//TODO 			knownstatus = KNOWN_SOME;
//TODO 	}
//TODO }
//TODO 
//TODO /* Identifies a file from this checksum */
//TODO void identify_file(const char* name)
//TODO {
//TODO 	FILE *f;
//TODO 	int length;
//TODO 	char* data;
//TODO 
//TODO 	f = fopen(name,"rb");
//TODO 	if (!f) {
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	/* determine length of file */
//TODO 	if (fseek (f, 0L, SEEK_END)!=0)	{
//TODO 		fclose(f);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	length = ftell(f);
//TODO 	if (length == -1L) {
//TODO 		fclose(f);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	/* empty file */
//TODO 	if (!length) {
//TODO 		fclose(f);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	/* allocate space for entire file */
//TODO 	data = (char*)malloc(length);
//TODO 	if (!data) {
//TODO 		fclose(f);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	if (fseek (f, 0L, SEEK_SET)!=0) {
//TODO 		free(data);
//TODO 		fclose(f);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	if (fread(data, 1, length, f) != length) {
//TODO 		free(data);
//TODO 		fclose(f);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	fclose(f);
//TODO 
//TODO 	identify_rom(name, crc32(0L,(const unsigned char*)data,length),length);
//TODO 
//TODO 	free(data);
//TODO }
//TODO 
//TODO void identify_zip(const char* zipname)
//TODO {
//TODO 	struct zipent* ent;
//TODO 
//TODO 	ZIP* zip = openzip( FILETYPE_RAW, 0, zipname );
//TODO 	if (!zip)
//TODO 		return;
//TODO 
//TODO 	while ((ent = readzip(zip))) {
//TODO 		/* Skip empty file and directory */
//TODO 		if (ent->uncompressed_size!=0) {
//TODO 			char* buf = (char*)malloc(strlen(zipname)+1+strlen(ent->name)+1);
//TODO //			sprintf(buf,"%s/%s",zipname,ent->name);
//TODO 			sprintf(buf,"%-12s",ent->name);
//TODO 			identify_rom(buf,ent->crc32,ent->uncompressed_size);
//TODO 			free(buf);
//TODO 		}
//TODO 	}
//TODO 
//TODO 	closezip(zip);
//TODO }
//TODO 
//TODO void romident(const char* name, int enter_dirs);
//TODO 
//TODO void identify_dir(const char* dirname)
//TODO {
//TODO 	DIR *dir;
//TODO 	struct dirent *ent;
//TODO 
//TODO 	dir = opendir(dirname);
//TODO 	if (!dir) {
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	ent = readdir(dir);
//TODO 	while (ent) {
//TODO 		/* Skip special files */
//TODO 		if (ent->d_name[0]!='.') {
//TODO 			char* buf = (char*)malloc(strlen(dirname)+1+strlen(ent->d_name)+1);
//TODO 			sprintf(buf,"%s/%s",dirname,ent->d_name);
//TODO 			romident(buf,0);
//TODO 			free(buf);
//TODO 		}
//TODO 
//TODO 		ent = readdir(dir);
//TODO 	}
//TODO 	closedir(dir);
//TODO }
//TODO 
//TODO void romident(const char* name,int enter_dirs) {
//TODO 	struct stat s;
//TODO 
//TODO 	if (stat(name,&s) != 0)	{
//TODO 		printf("%s: %s\n",name,strerror(errno));
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	if (S_ISDIR(s.st_mode)) {
//TODO 		if (enter_dirs)
//TODO 			identify_dir(name);
//TODO 	} else {
//TODO 		unsigned l = strlen(name);
//TODO 		if (l>=4 && stricmp(name+l-4,".zip")==0)
//TODO 			identify_zip(name);
//TODO 		else
//TODO 			identify_file(name);
//TODO 		return;
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO void CLIB_DECL terse_printf(const char *fmt,...)
//TODO {
//TODO 	/* no-op */
//TODO }
//TODO 
//TODO 
//TODO int CLIB_DECL compare_names(const void *elem1, const void *elem2)
//TODO {
//TODO 	struct GameDriver *drv1 = *(struct GameDriver **)elem1;
//TODO 	struct GameDriver *drv2 = *(struct GameDriver **)elem2;
//TODO 	char name1[200],name2[200];
//TODO 	namecopy(name1,drv1->description);
//TODO 	namecopy(name2,drv2->description);
//TODO 	return strcmp(name1,name2);
//TODO }
//TODO 
//TODO 
//TODO int CLIB_DECL compare_driver_names(const void *elem1, const void *elem2)
//TODO {
//TODO 	struct GameDriver *drv1 = *(struct GameDriver **)elem1;
//TODO 	struct GameDriver *drv2 = *(struct GameDriver **)elem2;
//TODO 	return strcmp(drv1->name, drv2->name);
//TODO }
//TODO 
//TODO 
    public static int frontend_help(String gamename) {
//TODO 	struct InternalMachineDriver drv;
        int i, j;
//TODO 	const char *all_games = "*";

        /* display help unless a game or an utility are specified */
        if (gamename == null && help == 0 && list == 0 && ident == 0 && verify == 0) {
            help = 1;
        }

        //System.out.println("LIST="+list);
        //System.out.println("help="+help);
        if (help != 0) /* brief help - useful to get current version info */ {
            printf("M.A.M.E. v%s - Multiple Arcade Machine Emulator\n"
                    + "Copyright (C) 1997-2003 by Nicola Salmoria and the MAME Team\n\n", build_version);
            showdisclaimer();
            printf("Usage:  MAME gamename [options]\n\n"
                    + "        MAME -list         for a brief list of supported games\n"
                    + "        MAME -listfull     for a full list of supported games\n"
                    + "        MAME -showusage    for a brief list of options\n"
                    + "        MAME -showconfig   for a list of configuration options\n"
                    + "        MAME -createconfig to create a mame.ini\n\n"
                    + "See readme.txt for a complete list of options.\n");
            return 0;
        }

//TODO 	/* HACK: some options REQUIRE gamename field to work: default to "*" */
//TODO 	if (!gamename || (strlen(gamename) == 0))
//TODO 		gamename = all_games;
//TODO 
//TODO 	/* sort the list if requested */
//TODO 	if (sortby)
//TODO 	{
//TODO 		int count = 0;
//TODO 
//TODO 		/* first count the drivers */
//TODO 		while (drivers[count]) count++;
//TODO 
//TODO 		/* qsort as appropriate */
//TODO 		if (sortby == 1)
//TODO 			qsort(drivers, count, sizeof(drivers[0]), compare_names);
//TODO 		else if (sortby == 2)
//TODO 			qsort(drivers, count, sizeof(drivers[0]), compare_driver_names);
//TODO 	}
//TODO 
        switch (list) /* front-end utilities ;) */ {
            case LIST_SHORT:
                /* simple games list */
                printf("\nMAME currently supports the following games:\n\n");
                for (i = j = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || (drivers[i].clone_of.flags & NOT_A_DRIVER) != 0) /*&& strwildcmp(gamename, drivers[i].name)==0*/) {
                        printf("%-8s", drivers[i].name);
                        j++;
                        if ((j % 8) == 0) {
                            printf("\n");
                        } else {
                            printf("  ");
                        }
                    }
                }
                if ((j % 8) != 0) {
                    printf("\n");
                }
                printf("\n");
                if (j != i) {
                    printf("Total ROM sets displayed: %4d - ", j);
                }
                printf("Total ROM sets supported: %4d\n", i);
                return 0;

            case LIST_FULL:
                /* games list with descriptions */
                printf("Name:     Description:\n");
                for (i = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || (drivers[i].clone_of.flags & NOT_A_DRIVER) != 0) /*&& !strwildcmp(gamename, drivers[i].name)*/) {
                        String name = "";

                        printf("%-10s", drivers[i].name);
                        name = namecopy(name, drivers[i].description);
                        printf("\"%s", name);

                        /* print the additional description only if we are listing clones */
                        if (listclones != 0) {
                            if (strchr(drivers[i].description, '(') != null) {
                                printf(" %s", strchr(drivers[i].description, '('));
                            }
                        }
                        printf("\"\n");
                    }
                }
                return 0;
//TODO 
//TODO 		case LIST_SAMDIR: /* games list with samples directories */
//TODO 			printf("Name:     Samples dir:\n");
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 				if ((listclones || drivers[i]->clone_of == 0
//TODO 						|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)
//TODO 						) && !strwildcmp(gamename, drivers[i]->name))
//TODO 				{
//TODO 					expand_machine_driver(drivers[i]->drv, &drv);
//TODO #if (HAS_SAMPLES || HAS_VLM5030)
//TODO 					for( j = 0; drv.sound[j].sound_type && j < MAX_SOUND; j++ )
//TODO 					{
//TODO 						const char **samplenames = null;
//TODO #if (HAS_SAMPLES)
//TODO 						if( drv.sound[j].sound_type == SOUND_SAMPLES )
//TODO 							samplenames = ((struct Samplesinterface *)drv.sound[j].sound_interface)->samplenames;
//TODO #endif
//TODO 						if (samplenames != 0 && samplenames[0] != 0)
//TODO 						{
//TODO 							printf("%-10s",drivers[i]->name);
//TODO 							if (samplenames[0][0] == '*')
//TODO 								printf("%s\n",samplenames[0]+1);
//TODO 							else
//TODO 								printf("%s\n",drivers[i]->name);
//TODO 						}
//TODO 					}
//TODO #endif
//TODO 				}
//TODO 			return 0;
//TODO 			break;
//TODO 
            case LIST_ROMS:
                /* game roms list or */
//TODO 		case LIST_SAMPLES: /* game samples list */
                j = 0;
                while (drivers[j] != null && (stricmp(gamename, drivers[j].name) != 0)) {
                    j++;
                }
                if (drivers[j] == null) {
                    printf("Game \"%s\" not supported!\n", gamename);
                    return 1;
                }
                gamedrv = drivers[j];
                if (list == LIST_ROMS) {
                    printromlist(gamedrv.rom, gamename);
                } else {
//TODO #if (HAS_SAMPLES || HAS_VLM5030)
//TODO 				int k;
//TODO 				expand_machine_driver(gamedrv->drv, &drv);
//TODO 				for( k = 0; drv.sound[k].sound_type && k < MAX_SOUND; k++ )
//TODO 				{
//TODO 					const char **samplenames = null;
//TODO #if (HAS_SAMPLES)
//TODO 					if( drv.sound[k].sound_type == SOUND_SAMPLES )
//TODO 							samplenames = ((struct Samplesinterface *)drv.sound[k].sound_interface)->samplenames;
//TODO #endif
//TODO 					if (samplenames != 0 && samplenames[0] != 0)
//TODO 					{
//TODO 						i = 0;
//TODO 						while (samplenames[i] != 0)
//TODO 						{
//TODO 							printf("%s\n",samplenames[i]);
//TODO 							i++;
//TODO 						}
//TODO 					}
//TODO                 }
//TODO #endif
                }
                return 0;
//TODO 
//TODO 		case LIST_LMR:
//TODO 			{
//TODO 				int total;
//TODO 
//TODO 				total = 0;
//TODO 				for (i = 0; drivers[i]; i++)
//TODO 						total++;
//TODO 				for (i = 0; drivers[i]; i++)
//TODO 				{
//TODO 					static int first_missing = 1;
//TODO //					get_rom_sample_path (argc, argv, i, null);
//TODO 					if (RomsetMissing (i))
//TODO 					{
//TODO 						if (first_missing)
//TODO 						{
//TODO 							first_missing = 0;
//TODO 							printf ("game      clone of  description\n");
//TODO 							printf ("--------  --------  -----------\n");
//TODO 						}
//TODO 						printf ("%-10s%-10s%s\n",
//TODO 								drivers[i]->name,
//TODO 								(drivers[i]->clone_of) ? drivers[i]->clone_of->name : "",
//TODO 								drivers[i]->description);
//TODO 					}
//TODO 					fprintf(stderr,"%d%%\r",100 * (i+1) / total);
//TODO 				}
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
            case LIST_DETAILS:
                /* A detailed MAMELIST.TXT type roms lister */

 /* First, we shall print the header */
                printf(" romname driver        ");
                for (j = 0; j < MAX_CPU; j++) {
                    printf("cpu %d    ", j + 1);
                }
                for (j = 0; j < MAX_SOUND; j++) {
                    printf("sound %d     ", j + 1);
                }
                printf("name\n");
                printf("-------- ------------- ");
                for (j = 0; j < MAX_CPU; j++) {
                    printf("-------- ");
                }
                for (j = 0; j < MAX_SOUND; j++) {
                    printf("----------- ");
                }
                printf("--------------------------\n");

                /* Let's cycle through the drivers */
                for (i = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || ((drivers[i].clone_of.flags & NOT_A_DRIVER) != 0)) /*&& !strwildcmp(gamename, drivers[i]->name)*/) {
                        /* Dummy structs to fetch the information from */
//TODO 
                        MachineCPU[] x_cpu;
//TODO 					const struct MachineSound *x_sound;
                        InternalMachineDriver x_driver = new InternalMachineDriver();

                        expand_machine_driver(drivers[i].drv, x_driver);
                        x_cpu = x_driver.cpu;
//TODO 					x_sound = x_driver.sound;

                        /* First, the rom name */
                        printf("%-8s ", drivers[i].name);

                        printf("%-15s ", drivers[i].source_file);

                        /* Then, cpus */
                        for (j = 0; j < MAX_CPU; j++) {
//TODO 						if (x_cpu[j].cpu_flags & CPU_AUDIO_CPU)
//TODO 							printf("[%-6s] ",cputype_name(x_cpu[j].cpu_type));
//TODO 						else
                            printf("%-8s ", cputype_name(x_cpu[j].cpu_type));
                        }
//TODO 
//TODO 					/* Then, sound chips */
//TODO 
//TODO 					for(j=0;j<MAX_SOUND;j++)
//TODO 					{
//TODO 						if (sound_num(&x_sound[j]))
//TODO 						{
//TODO 							printf("%dx",sound_num(&x_sound[j]));
//TODO 							printf("%-9s ",sound_name(&x_sound[j]));
//TODO 						}
//TODO 						else
//TODO 							printf("%-11s ",sound_name(&x_sound[j]));
//TODO 					}
//TODO 
//TODO 					/* Lastly, the name of the game and a \newline */
//TODO 
                        printf("%s\n", drivers[i].description);
                    }
                }
                return 0;
            case LIST_GAMELIST:
                /* GAMELIST.TXT */
                printf("This is the complete list of games supported by MAME %s.\n", build_version);
                if (listclones == 0) {
                    printf("Variants of the same game are not included, you can use the -listclones command\n"
                            + "to get a list of the alternate versions of a given game.\n");
                }
                printf("\n"
                        + "This list is generated automatically and is not 100%% accurate (particularly in\n"
                        + "the Screen Flip column). Please let us know of any errors so we can correct\n"
                        + "them.\n"
                        + "\n"
                        + "Here are the meanings of the columns:\n"
                        + "\n"
                        + "Working\n"
                        + "=======\n"
                        + "  NO: Emulation is still in progress; the game does not work correctly. This\n"
                        + "  means anything from major problems to a black screen.\n"
                        + "\n"
                        + "Correct Colors\n"
                        + "==============\n"
                        + "    YES: Colors should be identical to the original.\n"
                        + "  CLOSE: Colors are nearly correct.\n"
                        + "     NO: Colors are completely wrong. \n"
                        + "  \n"
                        + "  Note: In some cases, the color PROMs for some games are not yet available.\n"
                        + "  This causes a NO GOOD DUMP KNOWN message on startup (and, of course, the game\n"
                        + "  has wrong colors). The game will still say YES in this column, however,\n"
                        + "  because the code to handle the color PROMs has been added to the driver. When\n"
                        + "  the PROMs are available, the colors will be correct.\n"
                        + "\n"
                        + "Sound\n"
                        + "=====\n"
                        + "  PARTIAL: Sound support is incomplete or not entirely accurate. \n"
                        + "\n"
                        + "  Note: Some original games contain analog sound circuitry, which is difficult\n"
                        + "  to emulate. Therefore, these emulated sounds may be significantly different.\n"
                        + "\n"
                        + "Screen Flip\n"
                        + "===========\n"
                        + "  Many games were offered in cocktail-table models, allowing two players to sit\n"
                        + "  across from each other; the game's image flips 180 degrees for each player's\n"
                        + "  turn. Some games also have a \"Flip Screen\" DIP switch setting to turn the\n"
                        + "  picture (particularly useful with vertical games).\n"
                        + "  In many cases, this feature has not yet been emulated.\n"
                        + "\n"
                        + "Internal Name\n"
                        + "=============\n"
                        + "  This is the unique name that must be used when running the game from a\n"
                        + "  command line.\n"
                        + "\n"
                        + "  Note: Each game's ROM set must be placed in the ROM path, either in a .zip\n"
                        + "  file or in a subdirectory with the game's Internal Name. The former is\n"
                        + "  suggested, because the files will be identified by their CRC instead of\n"
                        + "  requiring specific names.\n\n");
                printf("+----------------------------------+-------+-------+-------+-------+----------+\n");
                printf("|                                  |       |Correct|       |Screen | Internal |\n");
                printf("| Game Name                        |Working|Colors | Sound | Flip  |   Name   |\n");
                printf("+----------------------------------+-------+-------+-------+-------+----------+\n");

                for (i = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || ((drivers[i].clone_of.flags & NOT_A_DRIVER) != 0)) /*&& !strwildcmp(gamename, drivers[i]->name)*/) {
                        String name_ref = "";

                        name_ref = namecopy(name_ref, drivers[i].description);

                        name_ref = strcat(name_ref, " ");

                        /* print the additional description only if we are listing clones */
                        if (listclones != 0) {
                            if (strchr(drivers[i].description, '(') != null) {
                                strcat(name_ref, strchr(drivers[i].description, '('));
                            }
                        }

                        printf("| %-33.33s", name_ref);

                        if ((drivers[i].flags & (GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION)) != 0) {
                            GameDriver maindrv;
                            int foundworking;

                            if (drivers[i].clone_of != null && (drivers[i].clone_of.flags & NOT_A_DRIVER) == 0) {
                                maindrv = drivers[i].clone_of;
                            } else {
                                maindrv = drivers[i];
                            }

                            foundworking = 0;
                            j = 0;
                            while (drivers[j] != null) {
                                if (drivers[j] == maindrv || drivers[j].clone_of == maindrv) {
                                    if ((drivers[j].flags & (GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION)) == 0) {
                                        foundworking = 1;
                                        break;
                                    }
                                }
                                j++;
                            }

                            if (foundworking != 0) {
                                printf("| No(1) ");
                            } else {
                                printf("|   No  ");
                            }
                        } else {
                            printf("|  Yes  ");
                        }

                        if ((drivers[i].flags & GAME_WRONG_COLORS) != 0) {
                            printf("|   No  ");
                        } else if ((drivers[i].flags & GAME_IMPERFECT_COLORS) != 0) {
                            printf("| Close ");
                        } else {
                            printf("|  Yes  ");
                        }

                        {
                            String[] samplenames = null;
// TODO 						expand_machine_driver(drivers[i].drv, &drv);
//TODO #if (HAS_SAMPLES || HAS_VLM5030)
//TODO 						for (j = 0;drv.sound[j].sound_type && j < MAX_SOUND; j++)
//TODO 						{
//TODO #if (HAS_SAMPLES)
//TODO 							if (drv.sound[j].sound_type == SOUND_SAMPLES)
//TODO 							{
//TODO 								samplenames = ((struct Samplesinterface *)drv.sound[j].sound_interface).samplenames;
//TODO 								break;
//TODO 							}
//TODO #endif
//TODO 						}
//TODO #endif
                            if ((drivers[i].flags & GAME_NO_SOUND) != 0) {
                                printf("|   No  ");
                            } else if ((drivers[i].flags & GAME_IMPERFECT_SOUND) != 0) {
                                if (samplenames != null) {
                                    printf("|Part(2)");
                                } else {
                                    printf("|Partial");
                                }
                            } else {
                                if (samplenames != null) {
                                    printf("| Yes(2)");
                                } else {
                                    printf("|  Yes  ");
                                }
                            }
                        }

                        if ((drivers[i].flags & GAME_NO_COCKTAIL) != 0) {
                            printf("|   No  ");
                        } else {
                            printf("|  Yes  ");
                        }

                        printf("| %-8s |\n", drivers[i].name);
                    }
                }

                printf("+----------------------------------+-------+-------+-------+-------+----------+\n\n");
                printf("(1) There are variants of the game (usually bootlegs) that work correctly\n");
//TODO #if (HAS_SAMPLES)
//TODO 			printf("(2) Needs samples provided separately\n");
//TODO #endif
                return 0;
//TODO 			break;

            case LIST_GAMES:
                /* list games, production year, manufacturer */
                for (i = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || ((drivers[i].clone_of.flags & NOT_A_DRIVER) != 0)) /*&& !strwildcmp(gamename, drivers[i]->description)*/) {
                        String name;

                        printf("%-5s%-36s ", drivers[i].year, drivers[i].manufacturer);

                        name = drivers[i].description;
                        printf("%s", name);

                        /* print the additional description only if we are listing clones */
                        if (listclones != 0) {
                            if (strchr(drivers[i].description, '(') != null) {
                                printf(" %s", strchr(drivers[i].description, '('));
                            }
                        }
                        printf("\n");
                    }
                }
                return 0;
//TODO 
//TODO 		case LIST_CLONES: /* list clones */
//TODO 			printf("Name:    Clone of:\n");
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 				if (drivers[i]->clone_of && !(drivers[i]->clone_of->flags & NOT_A_DRIVER) &&
//TODO 						(!strwildcmp(gamename,drivers[i]->name)
//TODO 								|| !strwildcmp(gamename,drivers[i]->clone_of->name)))
//TODO 					printf("%-8s %-8s\n",drivers[i]->name,drivers[i]->clone_of->name);
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_WRONGORIENTATION: /* list drivers which incorrectly use the orientation and visible area fields */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 			{
//TODO 				expand_machine_driver(drivers[i]->drv, &drv);
//TODO 				if ((drv.video_attributes & VIDEO_TYPE_VECTOR) == 0 &&
//TODO 						(drivers[i]->clone_of == 0
//TODO 								|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)) &&
//TODO 						drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1 <=
//TODO 						drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1)
//TODO 				{
//TODO 					if (strcmp(drivers[i]->name,"crater") &&
//TODO 						strcmp(drivers[i]->name,"mpatrol") &&
//TODO 						strcmp(drivers[i]->name,"troangel") &&
//TODO 						strcmp(drivers[i]->name,"travrusa") &&
//TODO 						strcmp(drivers[i]->name,"kungfum") &&
//TODO 						strcmp(drivers[i]->name,"battroad") &&
//TODO 						strcmp(drivers[i]->name,"vigilant") &&
//TODO 						strcmp(drivers[i]->name,"sonson") &&
//TODO 						strcmp(drivers[i]->name,"brkthru") &&
//TODO 						strcmp(drivers[i]->name,"darwin") &&
//TODO 						strcmp(drivers[i]->name,"exprraid") &&
//TODO 						strcmp(drivers[i]->name,"sidetrac") &&
//TODO 						strcmp(drivers[i]->name,"targ") &&
//TODO 						strcmp(drivers[i]->name,"spectar") &&
//TODO 						strcmp(drivers[i]->name,"venture") &&
//TODO 						strcmp(drivers[i]->name,"mtrap") &&
//TODO 						strcmp(drivers[i]->name,"pepper2") &&
//TODO 						strcmp(drivers[i]->name,"hardhat") &&
//TODO 						strcmp(drivers[i]->name,"fax") &&
//TODO 						strcmp(drivers[i]->name,"circus") &&
//TODO 						strcmp(drivers[i]->name,"robotbwl") &&
//TODO 						strcmp(drivers[i]->name,"crash") &&
//TODO 						strcmp(drivers[i]->name,"ripcord") &&
//TODO 						strcmp(drivers[i]->name,"starfire") &&
//TODO 						strcmp(drivers[i]->name,"fireone") &&
//TODO 						strcmp(drivers[i]->name,"renegade") &&
//TODO 						strcmp(drivers[i]->name,"battlane") &&
//TODO 						strcmp(drivers[i]->name,"megatack") &&
//TODO 						strcmp(drivers[i]->name,"killcom") &&
//TODO 						strcmp(drivers[i]->name,"challeng") &&
//TODO 						strcmp(drivers[i]->name,"kaos") &&
//TODO 						strcmp(drivers[i]->name,"formatz") &&
//TODO 						strcmp(drivers[i]->name,"bankp") &&
//TODO 						strcmp(drivers[i]->name,"liberatr") &&
//TODO 						strcmp(drivers[i]->name,"toki") &&
//TODO 						strcmp(drivers[i]->name,"stactics") &&
//TODO 						strcmp(drivers[i]->name,"sprint1") &&
//TODO 						strcmp(drivers[i]->name,"sprint2") &&
//TODO 						strcmp(drivers[i]->name,"nitedrvr") &&
//TODO 						strcmp(drivers[i]->name,"punchout") &&
//TODO 						strcmp(drivers[i]->name,"spnchout") &&
//TODO 						strcmp(drivers[i]->name,"armwrest") &&
//TODO 						strcmp(drivers[i]->name,"route16") &&
//TODO 						strcmp(drivers[i]->name,"stratvox") &&
//TODO 						strcmp(drivers[i]->name,"irobot") &&
//TODO 						strcmp(drivers[i]->name,"leprechn") &&
//TODO 						strcmp(drivers[i]->name,"starcrus") &&
//TODO 						strcmp(drivers[i]->name,"astrof") &&
//TODO 						strcmp(drivers[i]->name,"tomahawk") &&
//TODO 						1)
//TODO 						printf("%s %dx%d\n",drivers[i]->name,
//TODO 								drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1,
//TODO 								drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1);
//TODO 				}
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_WRONGFPS: /* list drivers with too high frame rate */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 			{
//TODO 				expand_machine_driver(drivers[i]->drv, &drv);
//TODO 				if ((drv.video_attributes & VIDEO_TYPE_VECTOR) == 0 &&
//TODO 						(drivers[i]->clone_of == 0
//TODO 								|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)) &&
//TODO 						drv.frames_per_second > 57 &&
//TODO 						drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1 > 244 &&
//TODO 						drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1 <= 256)
//TODO 				{
//TODO 					printf("%s %dx%d %fHz\n",drivers[i]->name,
//TODO 							drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1,
//TODO 							drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1,
//TODO 							drv.frames_per_second);
//TODO 				}
//TODO 			}
//TODO 			return 0;
//TODO 			break;

            case LIST_SOURCEFILE:
                for (i = 0; drivers[i] != null; i++) //TODO 				if (!strwildcmp(gamename,drivers[i]->name))
                {
                    printf("%-8s %s\n", drivers[i].name, drivers[i].source_file);
                }
                return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_GAMESPERSOURCEFILE:
//TODO 			{
//TODO 				#define MAXCOUNT 8
//TODO 
//TODO 				int numcount[MAXCOUNT],gamescount[MAXCOUNT];
//TODO 
//TODO 				for (i = 0;i < MAXCOUNT;i++) numcount[i] = gamescount[i] = 0;
//TODO 
//TODO 				for (i = 0; drivers[i]; i++)
//TODO 				{
//TODO 					if (drivers[i]->clone_of == 0 ||
//TODO 							(drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 					{
//TODO 						const char *sf = drivers[i]->source_file;
//TODO 						int total = 0;
//TODO 
//TODO 						for (j = 0; drivers[j]; j++)
//TODO 						{
//TODO 							if (drivers[j]->clone_of == 0 ||
//TODO 									(drivers[j]->clone_of->flags & NOT_A_DRIVER))
//TODO 							{
//TODO 								if (drivers[j]->source_file == sf)
//TODO 								{
//TODO 									if (j < i) break;
//TODO 
//TODO 									total++;
//TODO 								}
//TODO 							}
//TODO 						}
//TODO 
//TODO 						if (total)
//TODO 						{
//TODO 							if (total == 1)							{ numcount[0]++; gamescount[0] += total; }
//TODO 							else if (total >= 2 && total <= 3)		{ numcount[1]++; gamescount[1] += total; }
//TODO 							else if (total >= 4 && total <= 7)		{ numcount[2]++; gamescount[2] += total; }
//TODO 							else if (total >= 8 && total <= 15)		{ numcount[3]++; gamescount[3] += total; }
//TODO 							else if (total >= 16 && total <= 31)	{ numcount[4]++; gamescount[4] += total; }
//TODO 							else if (total >= 32 && total <= 63)	{ numcount[5]++; gamescount[5] += total; }
//TODO 							else if (total >= 64)					{ numcount[6]++; gamescount[6] += total; }
//TODO 						}
//TODO 					}
//TODO 				}
//TODO 
//TODO 				printf("1\t%d\t%d\n",		numcount[0],gamescount[0]);
//TODO 				printf("2-3\t%d\t%d\n",		numcount[1],gamescount[1]);
//TODO 				printf("4-7\t%d\t%d\n",		numcount[2],gamescount[2]);
//TODO 				printf("8-15\t%d\t%d\n",	numcount[3],gamescount[3]);
//TODO 				printf("16-31\t%d\t%d\n",	numcount[4],gamescount[4]);
//TODO 				printf("32-63\t%d\t%d\n",	numcount[5],gamescount[5]);
//TODO 				printf("64+\t%d\t%d\n",		numcount[6],gamescount[6]);
//TODO 
//TODO 				#undef MAXCOUNT
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_CRC: /* list all crc-32 */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 			{
//TODO 				const struct RomModule *region, *rom;
//TODO 
//TODO 				for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
//TODO 					for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 						printf("%08x %-12s %s\n",ROM_GETCRC(rom),ROM_GETNAME(rom),drivers[i]->description);
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_DUPCRC: /* list duplicate crc-32 (with different ROM name) */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 			{
//TODO 				const struct RomModule *region, *rom;
//TODO 
//TODO 				for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
//TODO 					for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 						if (ROM_GETCRC(rom))
//TODO 							for (j = i + 1; drivers[j]; j++)
//TODO 							{
//TODO 								const struct RomModule *region1, *rom1;
//TODO 
//TODO 								for (region1 = rom_first_region(drivers[j]); region1; region1 = rom_next_region(region1))
//TODO 									for (rom1 = rom_first_file(region1); rom1; rom1 = rom_next_file(rom1))
//TODO 										if (strcmp(ROM_GETNAME(rom), ROM_GETNAME(rom1)) && ROM_GETCRC(rom) == ROM_GETCRC(rom1))
//TODO 										{
//TODO 											printf("%08x %-12s %-8s <-> %-12s %-8s\n",ROM_GETCRC(rom),
//TODO 													ROM_GETNAME(rom),drivers[i]->name,
//TODO 													ROM_GETNAME(rom1),drivers[j]->name);
//TODO 										}
//TODO 							}
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 
//TODO 		case LIST_WRONGMERGE:	/* list duplicate crc-32 with different ROM name */
//TODO 								/* and different crc-32 with duplicate ROM name */
//TODO 								/* in clone sets */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 			{
//TODO 				const struct RomModule *region, *rom;
//TODO 
//TODO 				for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
//TODO 				{
//TODO 					for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 					{
//TODO 						if (ROM_GETCRC(rom))
//TODO 						{
//TODO 							for (j = 0; drivers[j]; j++)
//TODO 							{
//TODO 								if (j != i &&
//TODO 									drivers[j]->clone_of &&
//TODO 									(drivers[j]->clone_of->flags & NOT_A_DRIVER) == 0 &&
//TODO 									(drivers[j]->clone_of == drivers[i] ||
//TODO 									(i < j && drivers[j]->clone_of == drivers[i]->clone_of)))
//TODO 								{
//TODO 									const struct RomModule *region1, *rom1;
//TODO 									int match = 0;
//TODO 
//TODO 									for (region1 = rom_first_region(drivers[j]); region1; region1 = rom_next_region(region1))
//TODO 									{
//TODO 										for (rom1 = rom_first_file(region1); rom1; rom1 = rom_next_file(rom1))
//TODO 										{
//TODO 											if (!strcmp(ROM_GETNAME(rom), ROM_GETNAME(rom1)))
//TODO 											{
//TODO 												if (ROM_GETCRC(rom1) &&
//TODO 														ROM_GETCRC(rom) != ROM_GETCRC(rom1) &&
//TODO 														ROM_GETCRC(rom) != BADCRC(ROM_GETCRC(rom1)))
//TODO 												{
//TODO 													printf("%-12s %08x %-8s <-> %08x %-8s\n",ROM_GETNAME(rom),
//TODO 															ROM_GETCRC(rom),drivers[i]->name,
//TODO 															ROM_GETCRC(rom1),drivers[j]->name);
//TODO 												}
//TODO 												else
//TODO 													match = 1;
//TODO 											}
//TODO 										}
//TODO 									}
//TODO 
//TODO 									if (match == 0)
//TODO 									{
//TODO 										for (region1 = rom_first_region(drivers[j]); region1; region1 = rom_next_region(region1))
//TODO 										{
//TODO 											for (rom1 = rom_first_file(region1); rom1; rom1 = rom_next_file(rom1))
//TODO 											{
//TODO 												if (strcmp(ROM_GETNAME(rom), ROM_GETNAME(rom1)) && ROM_GETCRC(rom) == ROM_GETCRC(rom1))
//TODO 												{
//TODO 													printf("%08x %-12s %-8s <-> %-12s %-8s\n",ROM_GETCRC(rom),
//TODO 															ROM_GETNAME(rom),drivers[i]->name,
//TODO 															ROM_GETNAME(rom1),drivers[j]->name);
//TODO 												}
//TODO 											}
//TODO 										}
//TODO 									}
//TODO 								}
//TODO 							}
//TODO 						}
//TODO 					}
//TODO 				}
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_ROMSIZE: /* I used this for statistical analysis */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 			{
//TODO 				if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 				{
//TODO 					const struct RomModule *region, *rom, *chunk;
//TODO 					int romtotal = 0,romcpu = 0,romgfx = 0,romsound = 0;
//TODO 
//TODO 					for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
//TODO 					{
//TODO 						int type = ROMREGION_GETTYPE(region);
//TODO 
//TODO 						for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 						{
//TODO 							for (chunk = rom_first_chunk(rom); chunk; chunk = rom_next_chunk(chunk))
//TODO 							{
//TODO 								romtotal += ROM_GETLENGTH(chunk);
//TODO 								if (type >= REGION_CPU1 && type <= REGION_CPU8) romcpu += ROM_GETLENGTH(chunk);
//TODO 								if (type >= REGION_GFX1 && type <= REGION_GFX8) romgfx += ROM_GETLENGTH(chunk);
//TODO 								if (type >= REGION_SOUND1 && type <= REGION_SOUND8) romsound += ROM_GETLENGTH(chunk);
//TODO 							}
//TODO 						}
//TODO 					}
//TODO 
//TODO //					printf("%-8s\t%-5s\t%u\t%u\t%u\t%u\n",drivers[i]->name,drivers[i]->year,romtotal,romcpu,romgfx,romsound);
//TODO 					printf("%-8s\t%-5s\t%u\n",drivers[i]->name,drivers[i]->year,romtotal);
//TODO 				}
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_ROMDISTRIBUTION: /* I used this for statistical analysis */
//TODO 			{
//TODO 				int year;
//TODO 
//TODO 				for (year = 1975;year <= 2000;year++)
//TODO 				{
//TODO 					int gamestotal = 0,romcpu = 0,romgfx = 0,romsound = 0;
//TODO 
//TODO 					for (i = 0; drivers[i]; i++)
//TODO 					{
//TODO 						if (atoi(drivers[i]->year) == year)
//TODO 						{
//TODO 							if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 							{
//TODO 								const struct RomModule *region, *rom, *chunk;
//TODO 
//TODO 								gamestotal++;
//TODO 
//TODO 								for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
//TODO 								{
//TODO 									int type = ROMREGION_GETTYPE(region);
//TODO 
//TODO 									for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 									{
//TODO 										for (chunk = rom_first_chunk(rom); chunk; chunk = rom_next_chunk(chunk))
//TODO 										{
//TODO 											if (type >= REGION_CPU1 && type <= REGION_CPU8) romcpu += ROM_GETLENGTH(chunk);
//TODO 											if (type >= REGION_GFX1 && type <= REGION_GFX8) romgfx += ROM_GETLENGTH(chunk);
//TODO 											if (type >= REGION_SOUND1 && type <= REGION_SOUND8) romsound += ROM_GETLENGTH(chunk);
//TODO 										}
//TODO 									}
//TODO 								}
//TODO 							}
//TODO 						}
//TODO 					}
//TODO 
//TODO 					printf("%-5d\t%u\t%u\t%u\t%u\n",year,gamestotal,romcpu,romgfx,romsound);
//TODO 				}
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_ROMNUMBER: /* I used this for statistical analysis */
//TODO 			{
//TODO 				#define MAXCOUNT 100
//TODO 
//TODO 				int numcount[MAXCOUNT];
//TODO 
//TODO 				for (i = 0;i < MAXCOUNT;i++) numcount[i] = 0;
//TODO 
//TODO 				for (i = 0; drivers[i]; i++)
//TODO 				{
//TODO 					if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 					{
//TODO 						const struct RomModule *region, *rom;
//TODO 						int romnum = 0;
//TODO 
//TODO 						for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
//TODO 						{
//TODO 							for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
//TODO 							{
//TODO 								romnum++;
//TODO 							}
//TODO 						}
//TODO 
//TODO 						if (romnum)
//TODO 						{
//TODO 							if (romnum > MAXCOUNT) romnum = MAXCOUNT;
//TODO 							numcount[romnum-1]++;
//TODO 						}
//TODO 					}
//TODO 				}
//TODO 
//TODO 				for (i = 0;i < MAXCOUNT;i++)
//TODO 					printf("%d\t%d\n",i+1,numcount[i]);
//TODO 
//TODO 				#undef MAXCOUNT
//TODO 			}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_PALETTESIZE: /* I used this for statistical analysis */
//TODO 			for (i = 0; drivers[i]; i++)
//TODO 				if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 				{
//TODO 					expand_machine_driver(drivers[i]->drv, &drv);
//TODO 					printf("%-8s\t%-5s\t%u\n",drivers[i]->name,drivers[i]->year,drv.total_colors);
//TODO 				}
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 		case LIST_CPU: /* I used this for statistical analysis */
//TODO 			{
//TODO 				int type;
//TODO 
//TODO 				for (type = 1;type < CPU_COUNT;type++)
//TODO 				{
//TODO 					int count_main = 0,count_slave = 0;
//TODO 
//TODO 					i = 0;
//TODO 					while (drivers[i])
//TODO 					{
//TODO 						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 						{
//TODO 							struct InternalMachineDriver x_driver;
//TODO 							const struct MachineCPU *x_cpu;
//TODO 
//TODO 							expand_machine_driver(drivers[i]->drv, &x_driver);
//TODO 							x_cpu = x_driver.cpu;
//TODO 
//TODO 							for (j = 0;j < MAX_CPU;j++)
//TODO 							{
//TODO 								if (x_cpu[j].cpu_type == type)
//TODO 								{
//TODO 									if (j == 0) count_main++;
//TODO 									else count_slave++;
//TODO 									break;
//TODO 								}
//TODO 							}
//TODO 						}
//TODO 
//TODO 						i++;
//TODO 					}
//TODO 
//TODO 					printf("%s\t%d\n",cputype_name(type),count_main+count_slave);
//TODO //					printf("%s\t%d\t%d\n",cputype_name(type),count_main,count_slave);
//TODO 				}
//TODO 			}
//TODO 
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 
//TODO 		case LIST_CPUCLASS: /* I used this for statistical analysis */
//TODO 			{
//TODO 				int year;
//TODO 
//TODO //				for (j = 1;j < CPU_COUNT;j++)
//TODO //					printf("\t%s",cputype_name(j));
//TODO 				for (j = 0;j < 3;j++)
//TODO 					printf("\t%d",8<<j);
//TODO 				printf("\n");
//TODO 
//TODO 				for (year = YEAR_BEGIN;year <= YEAR_END;year++)
//TODO 				{
//TODO 					int count[CPU_COUNT];
//TODO 					int count_buswidth[3];
//TODO 
//TODO 					for (j = 0;j < CPU_COUNT;j++)
//TODO 						count[j] = 0;
//TODO 					for (j = 0;j < 3;j++)
//TODO 						count_buswidth[j] = 0;
//TODO 
//TODO 					i = 0;
//TODO 					while (drivers[i])
//TODO 					{
//TODO 						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 						{
//TODO 							struct InternalMachineDriver x_driver;
//TODO 							const struct MachineCPU *x_cpu;
//TODO 
//TODO 							expand_machine_driver(drivers[i]->drv, &x_driver);
//TODO 							x_cpu = x_driver.cpu;
//TODO 
//TODO 							if (atoi(drivers[i]->year) == year)
//TODO 							{
//TODO //								for (j = 0;j < MAX_CPU;j++)
//TODO j = 0;	// count only the main cpu
//TODO 								{
//TODO 									count[x_cpu[j].cpu_type]++;
//TODO 									switch(cputype_databus_width(x_cpu[j].cpu_type))
//TODO 									{
//TODO 										case  8: count_buswidth[0]++; break;
//TODO 										case 16: count_buswidth[1]++; break;
//TODO 										case 32: count_buswidth[2]++; break;
//TODO 									}
//TODO 								}
//TODO 							}
//TODO 						}
//TODO 
//TODO 						i++;
//TODO 					}
//TODO 
//TODO 					printf("%d",year);
//TODO //					for (j = 1;j < CPU_COUNT;j++)
//TODO //						printf("\t%d",count[j]);
//TODO 					for (j = 0;j < 3;j++)
//TODO 						printf("\t%d",count_buswidth[j]);
//TODO 					printf("\n");
//TODO 				}
//TODO 			}
//TODO 
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 
//TODO 		case LIST_NOSOUND: /* I used this for statistical analysis */
//TODO 			{
//TODO 				int year;
//TODO 
//TODO 				for (year = 1975;year <= 2000;year++)
//TODO 				{
//TODO 					int games=0,nosound=0;
//TODO 
//TODO 					i = 0;
//TODO 					while (drivers[i])
//TODO 					{
//TODO 						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 						{
//TODO 							if (atoi(drivers[i]->year) == year)
//TODO 							{
//TODO 								games++;
//TODO 								if (drivers[i]->flags & GAME_NO_SOUND) nosound++;
//TODO 							}
//TODO 						}
//TODO 
//TODO 						i++;
//TODO 					}
//TODO 
//TODO 					printf("%d\t%d\t%d\n",year,nosound,games);
//TODO 				}
//TODO 			}
//TODO 
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 
//TODO 		case LIST_SOUND: /* I used this for statistical analysis */
//TODO 			{
//TODO 				int type;
//TODO 
//TODO 				for (type = 1;type < SOUND_COUNT;type++)
//TODO 				{
//TODO 					int count = 0,minyear = 3000,maxyear = 0;
//TODO 
//TODO 					i = 0;
//TODO 					while (drivers[i])
//TODO 					{
//TODO 						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 						{
//TODO 							struct InternalMachineDriver x_driver;
//TODO 							const struct MachineSound *x_sound;
//TODO 
//TODO 							expand_machine_driver(drivers[i]->drv, &x_driver);
//TODO 							x_sound = x_driver.sound;
//TODO 
//TODO 							for (j = 0;j < MAX_SOUND;j++)
//TODO 							{
//TODO 								if (x_sound[j].sound_type == type)
//TODO 								{
//TODO 									int year = atoi(drivers[i]->year);
//TODO 
//TODO 									count++;
//TODO 
//TODO 									if (year > 1900)
//TODO 									{
//TODO 										if (year > maxyear) maxyear = year;
//TODO 										if (year < minyear) minyear = year;
//TODO 									}
//TODO 								}
//TODO 							}
//TODO 						}
//TODO 
//TODO 						i++;
//TODO 					}
//TODO 
//TODO 					if (count)
//TODO //						printf("%s (%d-%d)\t%d\n",soundtype_name(type),minyear,maxyear,count);
//TODO 						printf("%s\t%d\n",soundtype_name(type),count);
//TODO 				}
//TODO 			}
//TODO 
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 
//TODO 		case LIST_NVRAM: /* I used this for statistical analysis */
//TODO 			{
//TODO 				int year;
//TODO 
//TODO 				for (year = 1975;year <= 2000;year++)
//TODO 				{
//TODO 					int games=0,nvram=0;
//TODO 
//TODO 					i = 0;
//TODO 					while (drivers[i])
//TODO 					{
//TODO 						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 						{
//TODO 							struct InternalMachineDriver x_driver;
//TODO 
//TODO 							expand_machine_driver(drivers[i]->drv, &x_driver);
//TODO 
//TODO 							if (atoi(drivers[i]->year) == year)
//TODO 							{
//TODO 								games++;
//TODO 								if (x_driver.nvram_handler) nvram++;
//TODO 							}
//TODO 						}
//TODO 
//TODO 						i++;
//TODO 					}
//TODO 
//TODO 					printf("%d\t%d\t%d\n",year,nvram,games);
//TODO 				}
//TODO 			}
//TODO 
//TODO 			return 0;
//TODO 			break;
//TODO 
//TODO 
//TODO 		case LIST_INFO: /* list all info */
//TODO 			print_mame_info( stdout, drivers );
//TODO 			return 0;
        }
//TODO 
//TODO 	if (verify)  /* "verify" utilities */
//TODO 	{
//TODO 		int err = 0;
//TODO 		int correct = 0;
//TODO 		int incorrect = 0;
//TODO 		int res = 0;
//TODO 		int total = 0;
//TODO 		int checked = 0;
//TODO 		int notfound = 0;
//TODO 
//TODO 
//TODO 		for (i = 0; drivers[i]; i++)
//TODO 		{
//TODO 			if (!strwildcmp(gamename, drivers[i]->name))
//TODO 				total++;
//TODO 		}
//TODO 
//TODO 		for (i = 0; drivers[i]; i++)
//TODO 		{
//TODO 			if (strwildcmp(gamename, drivers[i]->name))
//TODO 				continue;
//TODO 
//TODO 			/* set rom and sample path correctly */
//TODO //			get_rom_sample_path (argc, argv, i, null);
//TODO 
//TODO 			if (verify & VERIFY_ROMS)
//TODO 			{
//TODO 				res = VerifyRomSet (i,(verify & VERIFY_TERSE) ? terse_printf : (verify_printf_proc)printf);
//TODO 
//TODO 				if (res == CLONE_NOTFOUND || res == NOTFOUND)
//TODO 				{
//TODO 					notfound++;
//TODO 					goto nextloop;
//TODO 				}
//TODO 
//TODO 				if (res == INCORRECT || res == BEST_AVAILABLE || (verify & VERIFY_VERBOSE))
//TODO 				{
//TODO 					printf ("romset %s ", drivers[i]->name);
//TODO 					if (drivers[i]->clone_of && !(drivers[i]->clone_of->flags & NOT_A_DRIVER))
//TODO 						printf ("[%s] ", drivers[i]->clone_of->name);
//TODO 				}
//TODO 			}
//TODO 			if (verify & VERIFY_SAMPLES)
//TODO 			{
//TODO 				const char **samplenames = null;
//TODO 				expand_machine_driver(drivers[i]->drv, &drv);
//TODO #if (HAS_SAMPLES || HAS_VLM5030)
//TODO  				for( j = 0; drv.sound[j].sound_type && j < MAX_SOUND; j++ )
//TODO 				{
//TODO #if (HAS_SAMPLES)
//TODO  					if( drv.sound[j].sound_type == SOUND_SAMPLES )
//TODO  						samplenames = ((struct Samplesinterface *)drv.sound[j].sound_interface)->samplenames;
//TODO #endif
//TODO 				}
//TODO #endif
//TODO 				/* ignore games that need no samples */
//TODO 				if (samplenames == 0 || samplenames[0] == 0)
//TODO 					goto nextloop;
//TODO 
//TODO 				res = VerifySampleSet (i,(verify_printf_proc)printf);
//TODO 				if (res == NOTFOUND)
//TODO 				{
//TODO 					notfound++;
//TODO 					goto nextloop;
//TODO 				}
//TODO 				printf ("sampleset %s ", drivers[i]->name);
//TODO 			}
//TODO 
//TODO 			if (res == NOTFOUND)
//TODO 			{
//TODO 				printf ("oops, should never come along here\n");
//TODO 			}
//TODO 			else if (res == INCORRECT)
//TODO 			{
//TODO 				printf ("is bad\n");
//TODO 				incorrect++;
//TODO 			}
//TODO 			else if (res == CORRECT)
//TODO 			{
//TODO 				if (verify & VERIFY_VERBOSE)
//TODO 					printf ("is good\n");
//TODO 				correct++;
//TODO 			}
//TODO 			else if (res == BEST_AVAILABLE)
//TODO 			{
//TODO 				printf ("is best available\n");
//TODO 				correct++;
//TODO 			}
//TODO 			if (res)
//TODO 				err = res;
//TODO 
//TODO nextloop:
//TODO 			checked++;
//TODO 			fprintf(stderr,"%d%%\r",100 * checked / total);
//TODO 		}
//TODO 
//TODO 		if (correct+incorrect == 0)
//TODO 		{
//TODO 			printf ("%s ", (verify & VERIFY_ROMS) ? "romset" : "sampleset" );
//TODO 			if (notfound > 0)
//TODO 				printf("\"%8s\" not found!\n",gamename);
//TODO 			else
//TODO 				printf("\"%8s\" not supported!\n",gamename);
//TODO 			return 1;
//TODO 		}
//TODO 		else
//TODO 		{
//TODO 			printf("%d %s found, %d were OK.\n", correct+incorrect,
//TODO 					(verify & VERIFY_ROMS)? "romsets" : "samplesets", correct);
//TODO 			if (incorrect > 0)
//TODO 				return 2;
//TODO 			else
//TODO 				return 0;
//TODO 		}
//TODO 		return 0;
//TODO 	}
//TODO 	if (ident)
//TODO 	{
//TODO 		if (ident == 2) silentident = 1;
//TODO 		else silentident = 0;
//TODO 
//TODO 		knownstatus = KNOWN_START;
//TODO 		romident(gamename,1);
//TODO 		if (ident == 2)
//TODO 		{
//TODO 			switch (knownstatus)
//TODO 			{
//TODO 				case KNOWN_START: printf("ERROR     %s\n",gamename); break;
//TODO 				case KNOWN_ALL:   printf("KNOWN     %s\n",gamename); break;
//TODO 				case KNOWN_NONE:  printf("UNKNOWN   %s\n",gamename); break;
//TODO 				case KNOWN_SOME:  printf("PARTKNOWN %s\n",gamename); break;
//TODO 			}
//TODO 		}
//TODO 		return 0;
//TODO 	}
//TODO 
        /* FIXME: horrible hack to tell that no frontend option was used */
        return 1234;
    }
}
